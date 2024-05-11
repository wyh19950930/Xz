package com.chuzhi.xzyx.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.StackAdapter;
import com.chuzhi.xzyx.R;

/**
 * @Author : wyh
 * @Time : On 2023/6/6 10:07
 * @Description : LocationTrackingListAdapter 定位追踪listAdapter
 */
public class LocationTrackingListAdapter extends StackAdapter<String> {
    public OnClickListener mOnClickListener;
    public LocationTrackingListAdapter(Context context) {
        super(context);
    }

    @Override
    public void bindView(String data, int position, CardStackView.ViewHolder holder) {
        if (holder instanceof ColorItemViewHolder) {
            ColorItemViewHolder h = (ColorItemViewHolder) holder;
            h.onBind(data, position);
        }
    }

    @Override
    protected CardStackView.ViewHolder onCreateView(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
//            case R.layout.list_card_item_larger_header:
//                view = getLayoutInflater().inflate(R.layout.list_card_item_larger_header, parent, false);
//                return new ColorItemLargeHeaderViewHolder(view);
//            case R.layout.list_card_item_with_no_header:
//                view = getLayoutInflater().inflate(R.layout.list_card_item_with_no_header, parent, false);
//                return new ColorItemWithNoHeaderViewHolder(view);
            default:
                view = getLayoutInflater().inflate(R.layout.list_location_tracking_card_item, parent, false);
                return new ColorItemViewHolder(view);
//            default:
//            view = getLayoutInflater().inflate(R.layout.list_card_item_larger_header, parent, false);
//            return new ColorItemLargeHeaderViewHolder(view);

        }
    }

     class ColorItemViewHolder extends CardStackView.ViewHolder {
        View mLayout;
        View mContainerContent;
        TextView mTextTitle;
        TextView mTextListCardGj;
        TextView mTextListCardJb;
        TextView mTextListCardZz;

        public ColorItemViewHolder(View view) {
            super(view);
            mLayout = view.findViewById(R.id.frame_list_card_item);
            mContainerContent = view.findViewById(R.id.container_list_content);
            mTextTitle = (TextView) view.findViewById(R.id.text_list_card_title);
            mTextListCardGj = (TextView) view.findViewById(R.id.text_list_card_gj);
            mTextListCardJb = (Button) view.findViewById(R.id.text_list_card_jb);
            mTextListCardZz = (TextView) view.findViewById(R.id.text_list_card_zz);
        }

        @Override
        public void onItemExpand(boolean b) {//卡片整体点击事件
//            mTextTitle.setVisibility(b ? View.VISIBLE : View.GONE);
        }

        public void onBind(String data, int position) {


//            mLayout.getBackground().setColorFilter(ContextCompat.getColor(getContext(), data), PorterDuff.Mode.SRC_IN);
//            mTextTitle.setText(String.valueOf(position));

            mTextListCardGj.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener!=null){
                        mOnClickListener.onClickListener(v,position);
                    }
                }
            });
            mTextListCardJb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener!=null){
                        mOnClickListener.onClickListener(v,position);
                    }
                }
            });
            mTextListCardZz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnClickListener!=null){
                        mOnClickListener.onClickListener(v,position);
                    }
                }
            });
        }

    }


    public void setOnClickListener(OnClickListener onClickListener){
        this.mOnClickListener = onClickListener;

    }

    public interface OnClickListener{
        void onClickListener(View view,int position);
    }


}
