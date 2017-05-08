package cc.icen.vochat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import cc.icen.vochat.R;
import cc.icen.vochat.activity.InCallActivity;
import cc.icen.vochat.utils.Person;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{

    private static final String TAG = "PersonAdapter";

    private Context mContext;

    private List<Person> mPersonList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView personImage;
        TextView personName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            personImage = (ImageView) view.findViewById(R.id.person_image);
            personName = (TextView) view.findViewById(R.id.person_name);
        }
    }

    public ContactsAdapter(List<Person> fruitList) {
        mPersonList = fruitList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.contacts_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Person person = mPersonList.get(position);
                Intent intent = new Intent(mContext, InCallActivity.class);
//                intent.putExtra(InCallActivity.PERSON_NAME, person.getName());
//                intent.putExtra(InCallActivity.PERSON_IMAGE_ID, person.getImageId());
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Person fruit = mPersonList.get(position);
        holder.personName.setText(fruit.getName());
        Glide.with(mContext).load(fruit.getImageId()).into(holder.personImage);
    }

    @Override
    public int getItemCount() {
        return mPersonList.size();
    }

}
