package com.jn.yyz.practise.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jn.yyz.practise.R;
import com.jn.yyz.practise.entity.FillIn;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FillInAdapter extends RecyclerView.Adapter<FillInAdapter.FillInViewHolder> {

    private List<FillIn> fillInList;

    private ContentCallback contentCallback;

    private boolean isInput = true;

    public boolean isInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public FillInAdapter(List<FillIn> fillInList) {
        this.fillInList = fillInList;
    }


    public List<FillIn> getFillInList() {
        return fillInList;
    }

    public void setFillInList(List<FillIn> fillInList) {
        this.fillInList = fillInList;
    }

    @NonNull
    @Override
    public FillInViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.practise_item_fill_in, parent, false);
        return new FillInViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FillInViewHolder holder, int position) {

        FillIn fillIn = fillInList.get(position);
        holder.setData(fillIn);
    }

    @Override
    public int getItemCount() {
        return fillInList.size();
    }


    public ContentCallback getContentCallback() {
        return contentCallback;
    }

    public void setContentCallback(ContentCallback contentCallback) {
        this.contentCallback = contentCallback;
    }

    public class FillInViewHolder extends RecyclerView.ViewHolder {

        FillIn fillIn;
        TextView fillin_tv_text;
        EditText fillin_et_input;

        public FillInViewHolder(@NonNull View itemView) {
            super(itemView);
            fillin_tv_text = itemView.findViewById(R.id.fillin_tv_text);
            fillin_et_input = itemView.findViewById(R.id.fillin_et_input);

            fillin_et_input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {


                    if (isInput) {

                        if (s.length() != 0) {

                            fillIn.setUsetInput(s.toString());
                            if (contentCallback != null) {

                                contentCallback.textChange();
                            }
                        } else {

                            fillIn.setUsetInput(null);
                            if (contentCallback != null) {

                                contentCallback.textChange();
                            }
                        }
                    }
                }
            });
        }


        public void setData(FillIn fillIn) {

            this.fillIn = fillIn;


            fillin_et_input.setEnabled(isInput);

            Pattern pattern = Pattern.compile("_+");
            Matcher matcher = pattern.matcher(fillIn.getWord());
            boolean a = matcher.find();

            if (a) {//填空

                fillin_tv_text.setVisibility(View.GONE);
                fillin_et_input.setVisibility(View.VISIBLE);
                fillin_et_input.setText(fillIn.getUsetInput());
            } else {//文本

                fillin_tv_text.setVisibility(View.VISIBLE);
                fillin_et_input.setVisibility(View.GONE);
                fillin_tv_text.setText(fillIn.getWord());
            }
        }
    }

    public interface ContentCallback {

        void textChange();
    }
}
