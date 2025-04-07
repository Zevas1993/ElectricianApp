package com.example.electricianapp.ui.jobs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.data.local.entity.JobEntity
import com.example.electricianapp.databinding.ItemJobBinding

class JobAdapter(private val onItemClicked: (JobEntity) -> Unit) :
    ListAdapter<JobEntity, JobAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val currentJob = getItem(position)
        holder.bind(currentJob)
        holder.itemView.setOnClickListener {
            onItemClicked(currentJob)
        }
    }

    class JobViewHolder(private val binding: ItemJobBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(job: JobEntity) {
            binding.textViewJobName.text = job.name
            binding.textViewJobAddress.text = job.address
            // Bind other fields as needed
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<JobEntity>() {
        override fun areItemsTheSame(oldItem: JobEntity, newItem: JobEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JobEntity, newItem: JobEntity): Boolean {
            return oldItem == newItem
        }
    }
}
