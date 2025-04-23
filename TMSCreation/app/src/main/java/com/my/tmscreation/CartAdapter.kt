package com.my.tmscreation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val userId: String,
    private val onCartChanged: () -> Unit // callback to update total
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.cart_item_image)
        val productName: TextView = view.findViewById(R.id.cart_item_name)
        val productPrice: TextView = view.findViewById(R.id.cart_item_price)
        val removeButton: Button = view.findViewById(R.id.remove_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.productName.text = item.name
        holder.productPrice.text = item.price

        Glide.with(holder.itemView.context)
            .load(item.imageResId)
            .into(holder.productImage)

        holder.removeButton.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("carts").child(userId)
            dbRef.child(item.key).removeValue()
                .addOnSuccessListener {
                    items.removeAt(position)
                    notifyItemRemoved(position)
                    onCartChanged()
                }
        }
    }

    override fun getItemCount(): Int = items.size
}
