package com.jn.yyz.practise.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jn.yyz.practise.R;
import com.jn.yyz.practise.entity.SoundFillIn;

import java.util.List;

public class SoundFillInAdapter extends RecyclerView.Adapter<SoundFillInAdapter.SoundFillInViewHolder> {

    private List<SoundFillIn> soundFillIns;

    private int position = -1;

    private ClickCallback clickCallback;

    private boolean isChoose = true;

    public SoundFillInAdapter(List<SoundFillIn> soundFillIns) {

        this.soundFillIns = soundFillIns;
    }


    @NonNull
    @Override
    public SoundFillInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_sound_fill_in, parent, false);
        return new SoundFillInViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundFillInViewHolder holder, int position) {

        SoundFillIn soundFillIn = soundFillIns.get(position);
        holder.setData(soundFillIn);
    }

    @Override
    public int getItemCount() {
        return soundFillIns.size();
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    public ClickCallback getClickCallback() {
        return clickCallback;
    }

    public void setClickCallback(ClickCallback clickCallback) {
        this.clickCallback = clickCallback;
    }

    public List<SoundFillIn> getSoundFillIns() {
        return soundFillIns;
    }

    public void setSoundFillIns(List<SoundFillIn> soundFillIns) {
        this.soundFillIns = soundFillIns;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public class SoundFillInViewHolder extends RecyclerView.ViewHolder {

        private SoundFillIn soundFillIn;
        private LinearLayout sound_ll_sound;
        private ImageView sound_iv_icon;

        public SoundFillInViewHolder(@NonNull View itemView) {
            super(itemView);
            sound_ll_sound = itemView.findViewById(R.id.sound_ll_sound);
            sound_iv_icon = itemView.findViewById(R.id.sound_iv_icon);

            sound_ll_sound.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isChoose) {

                        position = getAdapterPosition();
                        notifyDataSetChanged();
                        if (clickCallback != null) {

                            clickCallback.click(soundFillIn);
                        }
                    }
                }
            });
        }

        public void setData(SoundFillIn soundFillIn) {

            this.soundFillIn = soundFillIn;
            if (position == getAdapterPosition()) {

                sound_ll_sound.setBackgroundResource(R.drawable.shape_pic_blue);
            } else {

                sound_ll_sound.setBackgroundResource(R.drawable.shape_pic_gray);
            }
            if (soundFillIn.isPlaying()) {

                Glide.with(itemView.getContext()).load(R.mipmap.gif_laba2).into(sound_iv_icon);
            } else {

                Glide.with(itemView.getContext()).load(R.mipmap.laba).into(sound_iv_icon);
            }
        }
    }

    public interface ClickCallback {

        void click(SoundFillIn soundFillIn);
    }

}
