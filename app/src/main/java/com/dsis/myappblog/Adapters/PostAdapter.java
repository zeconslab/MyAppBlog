package com.dsis.myappblog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dsis.myappblog.Constants;
import com.dsis.myappblog.R;
import com.dsis.myappblog.models.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder>{

    private Context context;
    private ArrayList<Post> list;
    private ArrayList<Post> listAllPost;

    public PostAdapter(Context context, ArrayList<Post> list) {
        this.context = context;
        this.list = list;
        this.listAllPost = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_post, parent, false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = list.get(position);
        Picasso.get().load(Constants.BASE + "storage/profiles/" + post.getUser().getPhoto()).into(holder.imgProfile);
        Picasso.get().load(Constants.BASE + "storage/posts/" + post.getPhoto()).into(holder.imgPost);

        holder.txtNamePost.setText(post.getUser().getName());

        holder.txtDatePost.setText(post.getDate());

        holder.txtDescPost.setText(post.getDesc());
        holder.txtCommentsPost.setText( post.getComments() + " Comentarios" );

        holder.txtLikesPost.setText(post.getLikes() + " Me gusta" );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Post> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(listAllPost);
            } else {
                for (Post post : listAllPost) {
                    if (post.getDesc().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                        post.getUser().getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                        {
                            filteredList.add(post);
                        }
                    }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((Collection<? extends Post>) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    class PostHolder extends RecyclerView.ViewHolder {

        private TextView txtNamePost, txtDatePost, txtDescPost, txtLikesPost, txtCommentsPost;
        private CircleImageView imgProfile;
        private ImageView imgPost;
        private ImageButton btnPostOption, btnLikes, btnComments;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            txtNamePost = itemView.findViewById(R.id.txtPostName);
            txtDatePost = itemView.findViewById(R.id.txtPostDate);
            txtDescPost = itemView.findViewById(R.id.txtPostDesc);
            txtLikesPost = itemView.findViewById(R.id.txtPostLikes);
            txtCommentsPost = itemView.findViewById(R.id.txtPostComments);

            imgProfile = itemView.findViewById(R.id.imgPostProfile);
            imgPost = itemView.findViewById(R.id.imgPostPhoto);

            btnPostOption = itemView.findViewById(R.id.btnOptionPost);
            btnLikes = itemView.findViewById(R.id.btnPostLike);
            btnComments = itemView.findViewById(R.id.btnPostComment);

        }
    }
}
