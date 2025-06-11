package com.jn.yyz.practise.adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.model.bean.PronBean;

import java.io.IOException;
import java.util.List;

/**
 * 音标适配器
 */
public class PhoneticAdapter extends RecyclerView.Adapter<PhoneticAdapter.PhoneticViewHolder> {

    private List<PronBean.VowelDTO> vowelDTOList;

    private MediaPlayer mediaPlayer;

    private PronBean.VowelDTO playVowelDTO;

    public PhoneticAdapter(List<PronBean.VowelDTO> vowelDTOList) {

        this.vowelDTOList = vowelDTOList;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mp.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                if (playVowelDTO != null) {

                    try {
                        mp.reset();
                        mp.setDataSource(playVowelDTO.getWordAudio());
                        mp.prepareAsync();
                        playVowelDTO = null;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public PhoneticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_phonetic, parent, false);
        return new PhoneticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneticViewHolder holder, int position) {

        PronBean.VowelDTO vowelDTO = vowelDTOList.get(position);
        holder.setData(vowelDTO);
    }

    @Override
    public int getItemCount() {
        return vowelDTOList.size();
    }

    public class PhoneticViewHolder extends RecyclerView.ViewHolder {

        TextView phonetic_tv_pron;
        TextView phonetic_tv_word;

        PronBean.VowelDTO vowelDTO;

        public PhoneticViewHolder(@NonNull View itemView) {
            super(itemView);
            phonetic_tv_pron = itemView.findViewById(R.id.phonetic_tv_pron);
            phonetic_tv_word = itemView.findViewById(R.id.phonetic_tv_word);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    playVowelDTO = vowelDTO;
                    try {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(vowelDTO.getSound());
                        mediaPlayer.prepareAsync();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        public void setData(PronBean.VowelDTO vowelDTO) {

            this.vowelDTO = vowelDTO;
            phonetic_tv_pron.setText(vowelDTO.getPron());
            phonetic_tv_word.setText(vowelDTO.getWord());
        }
    }


}
