package com.lahsuak.apps.mylist.ui.fragments

import android.annotation.SuppressLint
import android.app.*
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.lahsuak.apps.mylist.R
import com.lahsuak.apps.mylist.data.SortOrder
import com.lahsuak.apps.mylist.databinding.FragmentAddUpdateBinding
import com.lahsuak.apps.mylist.data.model.SubTask
import com.lahsuak.apps.mylist.data.model.Task
import com.lahsuak.apps.mylist.receiver.AlarmReceiver
import com.lahsuak.apps.mylist.ui.MainActivity.Companion.isReceived
import com.lahsuak.apps.mylist.ui.MainActivity.Companion.notificationId
import com.lahsuak.apps.mylist.ui.adapters.SubTaskAdapter
import com.lahsuak.apps.mylist.ui.viewmodel.SubTaskViewModel
import com.lahsuak.apps.mylist.ui.viewmodel.TaskViewModel
import com.lahsuak.apps.mylist.util.Util.createNotification
import com.lahsuak.apps.mylist.util.Util.notifyUser
import com.lahsuak.apps.mylist.util.Util.requestCode
import com.lahsuak.apps.mylist.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddUpdateFragment : Fragment(R.layout.fragment_add_update), SubTaskAdapter.SubTaskListener {
    private lateinit var binding: FragmentAddUpdateBinding
    private val model: TaskViewModel by viewModels()
    private val subModel: SubTaskViewModel by viewModels()
    private val args: AddUpdateFragmentArgs by navArgs()

    private lateinit var navController: NavController
    private lateinit var subTaskAdapter: SubTaskAdapter
    private lateinit var task: Task
    private lateinit var searchView: SearchView
    private var actionMode: ActionMode? = null
    private val mCalendar = Calendar.getInstance()

    companion object {
        var selectedItem2: Array<Boolean>? = null
        var counter2 = 0
        var is_in_action_mode2 = false
        var is_select_all2 = false
    }

    @SuppressLint("NotifyDataSetChanged", "QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddUpdateBinding.bind(view)

        subTaskAdapter = SubTaskAdapter(requireContext(), this)

        setHasOptionsMenu(true)
        createNotification(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            task = model.getById(args.id)
            if (task.isImp) {
                binding.isImp.isChecked = true
            }
            if (task.reminder != null) {
                binding.timerTxt.text = task.reminder
                binding.cancelTimer.visibility = View.VISIBLE
            }
            if (isReceived && task.reminder != null) {
                if (notificationId == args.id) {
                    binding.cancelTimer.visibility = View.GONE
                    binding.timerTxt.text = getString(R.string.add_date_time)
                    isReceived = false
                    notificationId = -1
                    task.reminder = null
                    model.update(task)
                }
            }
        }
        navController = findNavController()

        binding.apply {
            todoRecyclerView.apply {
                adapter = subTaskAdapter
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val task = subTaskAdapter.currentList[viewHolder.adapterPosition]
                    subModel.onSubTaskSwiped(task)
                }
            }).attachToRecyclerView(todoRecyclerView)
        }

        subModel.taskId.value = args.id
        subModel.subTasks.observe(viewLifecycleOwner) {
            subTaskAdapter.submitList(it)
            var count = 0
            for (element in it) {
                if (element.isDone)
                    count++
            }
            binding.taskProgress.text = getString(R.string.task_progress, count, it.size)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            subModel.subTasksEvent.collect { event ->
                when (event) {
                    is SubTaskViewModel.SubTaskEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                subModel.onUndoDeleteClick(event.subTask)
                            }.show()
                    }
                    SubTaskViewModel.SubTaskEvent.NavigateToAllCompletedScreen -> {
                        val action =
                            AddUpdateFragmentDirections.actionGlobalDeleteAllCompletedDialogFragment()
                        navController.navigate(action)
                    }
                }
            }
        }
        binding.addBtn.setOnClickListener {
            val action = AddUpdateFragmentDirections.actionAddUpdateFragmentToRenameFragmentDialog(
                true,
                args.id,
                null, -1
            )
            navController.navigate(action)
        }

        val speakLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val result1 = data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val subTask = SubTask(args.id, result1!![0], isDone = false, false, 0)
                    subModel.insertSubTask(subTask)
                }
            }

        binding.soundTask.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                speakLauncher.launch(intent)
            } else {
                notifyUser(
                    requireContext(),
                    "Your device not supported for speech input"
                )
            }
        }

        binding.timerLayout.setOnClickListener {
            reminderShow()
        }
        binding.cancelTimer.setOnClickListener {
            cancelReminder()
            binding.cancelTimer.visibility = View.GONE
        }
        binding.isImp.setOnCheckedChangeListener { _, isChecked ->
            binding.isImp.isChecked = isChecked

            task.isImp = isChecked
            model.update(task)
        }
    }

    private fun cancelReminder() {
        val intent = Intent(requireActivity().baseContext, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity().baseContext, requestCode, intent, 0
        )
        val alarmManager =
            requireActivity().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        binding.timerTxt.text = getString(R.string.add_date_time)
        task.reminder = null
        model.update(task)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        val pendingQuery = model.searchQuery.value
        if (pendingQuery != null && pendingQuery.isNotEmpty()) {
            searchItem.expandActionView()
            searchView.setQuery(pendingQuery, false)
        }

        searchView.onQueryTextChanged {
            subModel.searchQuery.value = it
        }
        searchView.queryHint = "Search Task"

        menu.getItem(4).title = "Share"
        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.showTask).isChecked = subModel.preferencesFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sortByName -> {
                subModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.sortByOld -> {
                subModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }
            R.id.showTask -> {
                item.isChecked = !item.isChecked
                subModel.onHideCompleted(item.isChecked)
                true
            }
            R.id.delete_all_completed_task -> {
                subModel.onDeleteAllCompletedClick()
                true
            }
            R.id.setting -> {
                shareTask(getAllText())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getAllText(): String {
        var sendtxt: String = task.title.uppercase() + " :-"
        for (i in subTaskAdapter.currentList) {
            sendtxt += "\n" + i.subTitle
        }
        return sendtxt
    }

    override fun onItemClicked(subTask: SubTask, position: Int) {
        if (is_in_action_mode2) {
            if (selectedItem2!![position]) {
                selectedItem2!![position] = false
                counter2--
                actionMode!!.title = "${counter2}/${subTaskAdapter.currentList.size} Selected"
            } else {
                selectedItem2!![position] = true
                counter2++
                actionMode!!.title = "${counter2}/${subTaskAdapter.currentList.size} Selected"
            }
        } else {
            val action = AddUpdateFragmentDirections.actionAddUpdateFragmentToRenameFragmentDialog(
                true,
                args.id,
                subTask.subTitle,
                subTask.sId
            )
            navController.navigate(action)
        }
    }


    override fun onDeleteClicked(subTask: SubTask) {
        if (subTask.isDone) {
            subModel.showDeleteDialog(requireContext(), subTask)
        } else {
            val action = AddUpdateFragmentDirections.actionAddUpdateFragmentToRenameFragmentDialog(
                true,
                args.id,
                subTask.subTitle,
                subTask.sId
            )
            navController.navigate(action)
        }
    }

    override fun onCheckBoxClicked(subTask: SubTask, taskCompleted: Boolean) {
        subModel.onSubTaskCheckedChanged(subTask, taskCompleted)
    }

    private fun shareTask(text: String?) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            if (!text.isNullOrEmpty()) {
                putExtra(Intent.EXTRA_TEXT, text)
                type = "text/plain"
            }
        }
        try {
            startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            notifyUser(requireContext(), "Task is Empty")
        }
    }

    private val callback = object : ActionMode.Callback {
        override fun onCreateActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            val menuInflater = MenuInflater(requireContext())
            menuInflater.inflate(R.menu.action_menu, menu)
            return true
        }

        override fun onPrepareActionMode(
            mode: ActionMode?,
            menu: Menu?
        ): Boolean {
            return false
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onActionItemClicked(
            mode: ActionMode?,
            item: MenuItem?
        ): Boolean {
            return when (item?.itemId) {
                R.id.action_delete -> {
                    if (counter2 == 0) {
                        Toast.makeText(
                            requireContext(),
                            "Please select a task",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (selectedItem2!!.isNotEmpty()) {
                        if (counter2 == subTaskAdapter.currentList.size) {
                            //delete all tasks
                            showDialog(true)
                        } else {
                            //delete one by one
                            showDialog(false)
                        }
                    }
                    true
                }
                R.id.action_selectAll -> {
                    if (!is_select_all2) {
                        item.setIcon(R.drawable.ic_select_all_on)
                        for (i in 0 until subTaskAdapter.currentList.size)
                            selectedItem2!![i] == true

                        counter2 = subTaskAdapter.currentList.size
                        actionMode!!.title =
                            "${counter2}/${subTaskAdapter.currentList.size} Selected"
                        is_select_all2 = true
                    } else {
                        item.setIcon(R.drawable.ic_select_all)
                        for (i in 0 until subTaskAdapter.currentList.size)
                            selectedItem2!![i] == false

                        counter2 = 0
                        is_select_all2 = false
                        actionMode!!.title =
                            "${counter2}/${subTaskAdapter.currentList.size} Selected"
                    }
                    subTaskAdapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            onActionMode(false)
            actionMode = null
        }
    }

    override fun onAnyItemLongClicked(position: Int) {
        if (!is_in_action_mode2) {
            onActionMode(true)
            counter2 = 1
            selectedItem2!![position] = true
        } else {
            if (selectedItem2!![position]) {
                selectedItem2!![position] = false
                counter2--
            } else {
                selectedItem2!![position] = true
                counter2++
            }
        }
        if (actionMode == null) {
            actionMode =
                (activity as AppCompatActivity).startSupportActionMode(callback)!!
        }
        actionMode!!.title = "${counter2}/${subTaskAdapter.currentList.size} Selected"
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onActionMode(actionModeOn: Boolean) {
        if (actionModeOn) {
            selectedItem2 = Array(subTaskAdapter.currentList.size) { false }
            is_in_action_mode2 = true
            binding.soundTask.visibility = View.GONE
            binding.addBtn.visibility = View.GONE
        } else {
            is_in_action_mode2 = false
            is_select_all2 = false
            binding.addBtn.visibility = View.VISIBLE
            binding.soundTask.visibility = View.VISIBLE
            subTaskAdapter.notifyDataSetChanged()
        }
    }

    private fun showDialog(isAllDeleted: Boolean) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Confirm deletion")
            .setMessage("Do you want to delete all the tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { dialog, _ ->
                if (isAllDeleted) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        subModel.deleteAllSubTasks(args.id)
                    }
                } else {
                    for (i in selectedItem2!!.indices) {
                        if (selectedItem2!![i]) {
                            subModel.deleteSubTask(subTaskAdapter.currentList[i])
                        }
                    }
                }
                counter2 = 0
                actionMode!!.finish()
                onActionMode(false)
                notifyUser(requireContext(), "Tasks are deleted successfully")
                dialog.dismiss()
            }.show()
    }


    private fun reminderShow() {

        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val am_pm = formatter.format(mCalendar.time).substringAfter(" ")
        val hour = formatter.format(mCalendar.time).substring(0, 2).trim()
        val min = formatter.format(mCalendar.time).substring(3, 5).trim()

        val h: Int
        if (am_pm == "pm")
            h = hour.toInt() + 12
        else
            h = hour.toInt()
        val materialTimePicker: MaterialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("SET TIME")
            .setHour(h)
            .setMinute(min.toInt())
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .build()

        //DATE PICKER LOGIC
        val dateListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mCalendar.set(Calendar.YEAR, year)
                mCalendar.set(Calendar.MONTH, monthOfYear)
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val df = SimpleDateFormat("DD MMM,yy hh:mm a", Locale.getDefault())

                materialTimePicker.show(requireActivity().supportFragmentManager, "TIME")
                // dialog update the TextView accordingly
                materialTimePicker.addOnPositiveButtonClickListener {
                    val pickedHour: Int = materialTimePicker.hour
                    val pickedMinute: Int = materialTimePicker.minute

                    mCalendar.set(Calendar.HOUR_OF_DAY, pickedHour)
                    mCalendar.set(Calendar.MINUTE, pickedMinute)
                    mCalendar.set(Calendar.SECOND, 0)

                    val time = df.format(mCalendar.time)
                    binding.timerTxt.text = time
                    binding.cancelTimer.visibility = View.VISIBLE

                    val intent = Intent(requireActivity().baseContext, AlarmReceiver::class.java)
                    intent.putExtra("task", "${task.id},${task.title}")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    val pendingIntent = PendingIntent.getBroadcast(
                        requireActivity().baseContext, task.id, intent, 0
                    )
                    Log.d("TAG", "reminderShow: ${task.id} and ${task.title},$time")

                    val alarmManager =
                        requireActivity().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        mCalendar.timeInMillis,//+ milliSeconds,
                        pendingIntent
                    )

                    // then update the task
                    task.reminder = time
                    model.update(task)
                }

            }
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateListener,
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }

}