package com.tencent.rapidview.control;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.rapidview.deobfuscated.IRapidView;
import com.tencent.rapidview.deobfuscated.control.IPhotonPagerAdapter;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceLuaToJava;

import java.util.ArrayList;
import java.util.List;


/**
 * @Class PhotonPagerAdapter
 * @Desc 给viewpager提供的adapter
 *
 * @author arlozhang
 * @date 2017.07.03
 */

public class RapidPagerAdapter extends PagerAdapter implements IPhotonPagerAdapter {

    private List<IRapidView> mListView = new ArrayList<IRapidView>();

    private int mAddFlag = 0;

    public RapidPagerAdapter(List<IRapidView> listView){
        refresh(listView);
    }

    @Override
    public IRapidView getChildView(String name){
        IRapidView child = null;

        for( int i = 0; i < mListView.size(); i++ ){
            child = mListView.get(i).getParser().getChildView(name);

            if( child == null ){
                continue;
            }

            break;
        }

        return child;
    }

    @Override
    public void addView(IRapidView view){
        if( view == null ){
            return;
        }

        mListView.add(view);
        notifyDataSetChanged();
    }

    public IRapidView getView(int i){
        return mListView.get(i);
    }

    public void refresh(LuaTable tableView){
        List<IRapidView> list = null;

        if( tableView == null ){
            return;
        }

        list = translateList(tableView);

        refresh(list);
    }

    public void refresh(List<IRapidView> list){
        if( list == null ){
            return;
        }

        mListView.clear();
        mListView.addAll(list);
        notifyDataSetChanged();
    }

    private List<IRapidView> translateList(LuaTable table){
        LuaValue key = LuaValue.NIL;
        LuaValue value = LuaValue.NIL;
        List<IRapidView> list = new ArrayList<IRapidView>();

        if( table == null || !table.istable() ){
            return list;
        }

        while(true){
            Object obj = null;
            Varargs argsItem = table.next(key);
            key = argsItem.arg1();

            if( key.isnil() ){
                break;
            }
            value = argsItem.arg(2);
            obj = CoerceLuaToJava.coerce(value, Object.class);

            if( obj instanceof IRapidView ){
                list.add((IRapidView)obj);
            }
        }

        return list;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int flag = mAddFlag >> position & 1;

        if(flag != 1) {
            container.addView(mListView.get(position).getView(), mListView.get(position).getParser().getParams().getLayoutParams());
            mAddFlag += 1 << position;
        }

        return mListView.get(position).getView();
    }

    @Override
    public int getCount() {
        return mListView.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}