package com.intelligent_chest.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.intelligentchest.R;
import com.intelligent_chest.entity.ClothesEntity;
import com.intelligent_chest.loader.ImageLoader;

import java.util.List;

/**
 * 用于从服务器加载查看所有衣服listView的数据
*/
public class ClothesListViewAdapter extends BaseAdapter implements OnScrollListener{
    private List<ClothesEntity> mList;
    private ImageLoader mImageLoader;
    private ListView mListView;
    private Context mContext;
    private ClothesEntity mClothesDetailsEntity;

    public ClothesListViewAdapter(Context context, List<ClothesEntity> data, ListView listView) {
        this.mList = data;
        this.mListView = listView;
        this.mContext = context;
        mImageLoader = new ImageLoader(mListView);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.lv_clothes_items, null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.id_img_clothes);
            viewHolder.sex = (TextView) convertView.findViewById(R.id.id_tv_sex);
            viewHolder.brand = (TextView) convertView.findViewById(R.id.id_tv_brand);
            viewHolder.size = (TextView) convertView.findViewById(R.id.id_tv_size);
            viewHolder.style = (TextView) convertView.findViewById(R.id.id_tv_style);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        mClothesDetailsEntity = mList.get(position);
        // 测试使用图片
        viewHolder.imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher));
        // 标记通信使用图片，使其正确显示图片而不受缓存影响导致图片显示在不对应的item上
//        viewHolder.imageView.setTag(mClothesDetailsEntity.getId());
        mImageLoader.showImage(mClothesDetailsEntity.getId(), viewHolder.imageView);
        viewHolder.sex.setText("性别:" + mClothesDetailsEntity.getSex());
        viewHolder.brand.setText("品牌:" + mClothesDetailsEntity.getBrand());
        viewHolder.size.setText("尺码:" + mClothesDetailsEntity.getSize());
        viewHolder.style.setText("服装类型:" + mClothesDetailsEntity.getStyle());
        notifyDataSetChanged();//获取到数据后需要刷新lv

        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView sex, brand, size, style;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {

    }
}
