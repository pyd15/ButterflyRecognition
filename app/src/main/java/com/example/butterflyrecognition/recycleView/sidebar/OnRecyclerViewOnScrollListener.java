package com.example.butterflyrecognition.recycleView.sidebar;

/**
 * Created by Dr.P on 2017/11/10.
 * runas /user:Dr.P "cmd /k"
 */

public class OnRecyclerViewOnScrollListener{
//        extends
//        RecyclerView.OnScrollListener implements OnPositionListener {
//
//
//
//
//    public static enum LAYOUT_MANAGER_TYPE {
//        LINEAR, GRID, STAGGERED_GRID
//    }
//
//
//    /**
//     * layoutManager的类型（枚举）
//     */
//    protected LAYOUT_MANAGER_TYPE layoutManagerType;
//
//
//
//
//    /**
//     * 第一个可见的item的位置
//     */
//    private int firstVisibleItemPosition;
//
//
//    @Override
//    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//        super.onScrolled(recyclerView, dx, dy);
//        RecyclerView.LayoutManager layoutManager = recyclerView
//                .getLayoutManager();
//        if (layoutManagerType == null) {
//            if (layoutManager instanceof LinearLayoutManager) {
//                layoutManagerType = LAYOUT_MANAGER_TYPE.LINEAR;
//            } else if (layoutManager instanceof GridLayoutManager) {
//                layoutManagerType = LAYOUT_MANAGER_TYPE.GRID;
//            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
//                layoutManagerType = LAYOUT_MANAGER_TYPE.STAGGERED_GRID;
//            } else {
//                throw new RuntimeException(
//                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
//            }
//        }
//
//
//        switch (layoutManagerType) {
//            case LINEAR:
//                firstVisibleItemPosition = ((LinearLayoutManager) layoutManager)
//                        .findFirstCompletelyVisibleItemPosition();
//                break;
//            case GRID:
//                firstVisibleItemPosition = ((GridLayoutManager) layoutManager)
//                        .findFirstCompletelyVisibleItemPosition();
//                break;
//            default:
//                break;
//        }
//        Log.i("OnRecyclerViewOnScrollListener", "firstVisibleItemPosition:"+firstVisibleItemPosition);
//    }
//
//
//    @Override
//    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//        super.onScrollStateChanged(recyclerView, newState);
//        onPosition(firstVisibleItemPosition);
//    }
//
//
//    @Override
//    public void onPosition(int firstCompleteVisiblePosition) {
//    }
//}
//
//mRecyclerView.setOnScrollListener(new OnRecyclerViewOnScrollListener() {
//@Override
//public void onPosition(int firstCompleteVisiblePosition) {
//        mSideBar.setSelected(getSortBarCharacterFromRecyclerViewPosition(firstCompleteVisiblePosition));
//        }
//        });
//private String getSortBarCharacterFromRecyclerViewPosition(int position) {
//        Log.i("OnRecyclerViewOnScrollListener","getSortBarCharacterFromRecyclerViewPosition:"+position);
//        if(mRecyclerAdapter.mCursor!=null&&mRecyclerAdapter.mCursor.getCount()>position){
//        mRecyclerAdapter.mCursor.moveToPosition(position);
//        Character c = mRecyclerAdapter.mCursor.getString(
//        你的拼音字段的index).charAt(0);
//        if (c.equals('[')) {
//        return "#";
//        }
//        return c + "";
//        }else{
//        return null;
//        }
        }
