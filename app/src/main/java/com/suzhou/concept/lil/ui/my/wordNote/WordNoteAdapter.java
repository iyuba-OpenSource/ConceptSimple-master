package com.suzhou.concept.lil.ui.my.wordNote;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suzhou.concept.R;
import com.suzhou.concept.lil.data.newDB.word.collect.WordCollectBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title:
 * @date: 2023/10/8 16:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordNoteAdapter extends RecyclerView.Adapter<WordNoteAdapter.NoteHolder> {

    private Context context;
    private List<WordCollectBean> list;

    //当前的编辑状态
    private boolean showEdit = false;
    //预存选中的数据
    private Map<String,WordCollectBean> saveMap = new HashMap<>();

    public WordNoteAdapter(Context context, List<WordCollectBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_word_note,parent,false);
        return new NoteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        if (holder==null){
            return;
        }

        WordCollectBean collectBean = list.get(position);
        holder.word.setText(collectBean.word);
        if (TextUtils.isEmpty(collectBean.pron)){
            holder.pron.setVisibility(View.GONE);
        }else {
            holder.pron.setVisibility(View.VISIBLE);
            holder.pron.setText("["+collectBean.pron+"]");
        }
        if (TextUtils.isEmpty(collectBean.def)){
            holder.def.setVisibility(View.GONE);
        }else {
            holder.def.setVisibility(View.VISIBLE);
            holder.def.setText(collectBean.def);
        }

        //根据状态显示
        if (showEdit){
            holder.check.setVisibility(View.VISIBLE);
        }else {
            holder.check.setVisibility(View.GONE);
        }

        //根据选中状态显示
        WordCollectBean selectData = saveMap.get(collectBean.word);
        if (selectData==null){
            holder.check.setImageResource(R.drawable.ic_unseelct);
        }else {
            holder.check.setImageResource(R.drawable.ic_selected);
        }

        //点击
        holder.audio.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onAudioPlay(collectBean.audio);
            }
        });
        holder.check.setOnClickListener(v->{
            WordCollectBean bean = saveMap.get(collectBean.word);
            if (bean!=null){
                saveMap.remove(collectBean.word);
            }else {
                saveMap.put(collectBean.word,collectBean);
            }
            notifyItemChanged(position);

            //二次处理
            if (onItemClickListener!=null){
                if (saveMap!=null&&saveMap.keySet().size()>0){
                    onItemClickListener.onSelectData(saveMap);
                }else {
                    onItemClickListener.onSelectData(null);
                }
            }
        });
        holder.itemView.setOnClickListener(v->{
            if (onItemClickListener!=null){
                onItemClickListener.onItemClick(collectBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NoteHolder extends RecyclerView.ViewHolder{

        private ImageView audio;
        private TextView word,pron,def;
        private ImageView check;

        public NoteHolder(View rootView){
            super(rootView);

            audio = rootView.findViewById(R.id.audio);
            word = rootView.findViewById(R.id.word);
            pron = rootView.findViewById(R.id.pron);
            def = rootView.findViewById(R.id.def);
            check = rootView.findViewById(R.id.check);
        }
    }

    //刷新数据
    public void refreshData(List<WordCollectBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //增加数据
    public void addData(List<WordCollectBean> addList){
        this.list.addAll(addList);
        notifyDataSetChanged();
    }

    //设置编辑状态
    public void setEditStatus(boolean isEdit){
        this.showEdit = isEdit;
        notifyDataSetChanged();
    }

    //获取选中数据
    public List<WordCollectBean> getSelectData(){
        List<WordCollectBean> selectList = new ArrayList<>();
        if (saveMap!=null&&saveMap.keySet().size()>0){
            for (String key:saveMap.keySet()){
                selectList.add(saveMap.get(key));
            }
        }
        return selectList;
    }

    //清空选中的数据
    public void clearSelectData(){
        saveMap.clear();

        //同步刷新外部数据
        if (onItemClickListener!=null){
            onItemClickListener.onSelectData(null);
        }
    }

    //点击回调
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        //音频播放
        void onAudioPlay(String audioUrl);

        //item点击
        void onItemClick(WordCollectBean bean);

        //选中回调
        void onSelectData(Map<String,WordCollectBean> saveMap);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
