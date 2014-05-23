package dswork.android.lib.ui.MultiCheck;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import dswork.android.lib.R;
import dswork.android.lib.db.BaseModel;
import dswork.android.lib.ui.MultiCheck.MultiCheckListView2.ViewCache;

public class MultiCheckAdapter2 extends BaseAdapter 
{
	private Context ctx;//上下文
	private List dataList;//在绑定的数据
	private int itemLayoutRes;//绑定的条目布局资源(如:R.layout.item)
	private String[] from;
	private int[] to;
	private ViewCache vc;
	private int itemIdRes;//每条item项的主键TextView资源
	private int itemChkRes;//每条item项的CheckBox资源
	private int itemMenuRes;//每条item项的ImageButton资源
	protected LayoutInflater inflater;//通过XML生成view对象，属于系统服务
	protected static HashMap<Integer,Boolean> isSelected;// 用来控制CheckBox的选中状况  
	protected boolean isMultiChoose; //是否让listView变成多选模式
	private int itemMenuDialogTitleRes;//itemMenu对话框标题资源
	private int itemMenuDialogItemsRes;//itemMenu对话框项资源
	private ItemMenuDialog listener;//itemMenu对话框项点击监听器
	
	/**
	 * 构造器
	 * @param context 上下文
	 * @param dataList 数据集合
	 * @param itemLayoutRes 绑定的条目界面(如:R.layout.item)
	 * @param from 需显示的model属性名,字符串数组String[]
	 * @param to 属性名绑定的View控件id,整形数组int[]
	 * @param vc ViewCache视图缓存对象
	 */
	public MultiCheckAdapter2(Context ctx, List dataList, int itemLayoutRes, String[] from, int[] to, ViewCache vc) 
	{
		this.ctx = ctx;
		this.dataList = dataList;
		this.itemLayoutRes = itemLayoutRes;
		this.from = from;
		this.to = to;
		this.vc = vc;
		this.itemIdRes = R.id.itemId;
		this.itemChkRes = R.id.itemChk;
		this.itemMenuRes = R.id.itemMenu;
		this.itemMenuDialogTitleRes = R.string.item_menu_title;
		this.itemMenuDialogItemsRes = R.array.item_menu;
		this.inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.isMultiChoose = false;
		initData();
	}
	
	// 初始化isSelected的数据  
    private void initData()
    {  
        //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。         
        isSelected = new HashMap<Integer, Boolean>();      
        for (int i = 0; i < dataList.size(); i++) isSelected.put(i, false);    
    }  
    
	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int pos) {
		return dataList.get(pos);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		BaseModel o = (BaseModel) dataList.get(position);
		convertView = inflater.inflate(itemLayoutRes, null);
		//找到显示控件
		ViewCache cache = new ViewCache();
		cache.chk = (CheckBox)convertView.findViewById(itemChkRes);
		cache.idView = (TextView)convertView.findViewById(itemIdRes);
		cache.itemMenu = (ImageButton) convertView.findViewById(itemMenuRes);
		try
		{
			for(int i=0;i<to.length;i++)
			{
				Method m = o.getClass().getMethod("get"+from[i].substring(0,1).toUpperCase()+from[i].substring(1));
				((TextView)convertView.findViewById(to[i])).setText(String.valueOf(m.invoke(o)));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//保存视图缓存对象至View的Tag属性中
		convertView.setTag(cache);
		//判断是否显示checkbox和ctrlMenu
		if(isMultiChoose) 
		{
			cache.chk.setVisibility(CheckBox.VISIBLE);
			cache.itemMenu.setVisibility(ImageButton.GONE);
		}
		else 
		{
			cache.chk.setVisibility(CheckBox.GONE);
			cache.itemMenu.setVisibility(ImageButton.VISIBLE);
		}
		//id赋值，根据isSelected来设置checkbox的选中状况 
		if(null!=getIsSelected().get(position))
		{
			cache.chk.setChecked(getIsSelected().get(position));
			cache.idView.setText(o.getId().toString());
			cache.itemMenu.setTag(cache.idView);//保存记录id到Tag中方便后续操作
		}
		//单击每项的ctrlMenu弹出菜单
		cache.itemMenu.setOnClickListener(new ItemMenuOnClickListener());
		return convertView;
	}

	/**
	 * 获取已选Map
	 * @return
	 */
	public static HashMap<Integer,Boolean> getIsSelected() {  
        return isSelected;  
    }  
  
	/**
	 * 修改已选Map
	 * @param isSelected
	 */
    public static void setIsSelected(HashMap<Integer,Boolean> isSelected) {  
    	MultiCheckAdapter2.isSelected = isSelected;  
    }  
      
    /**
     * 修改多选模式
     * @param b
     */
    public void setIsMultiChoose(Boolean b){  
        isMultiChoose = b;  
    }	
    
    //ItemMenu项点击监听类
    private class ItemMenuOnClickListener implements OnClickListener
    {
		@Override
		public void onClick(final View v) 
		{
//			Log.i("itemMenuClick", String.valueOf(((TextView)v.getTag()).getText()));
//			Toast.makeText(context, ((TextView)v.getTag()).getText(), Toast.LENGTH_SHORT).show();
			itemMenuDialogTitleRes = listener.setItemMenuDialogTitleRes();
			itemMenuDialogItemsRes = listener.setItemMenuDialogItemsRes();
			new AlertDialog.Builder(ctx)
			.setTitle(itemMenuDialogTitleRes)
			.setItems(itemMenuDialogItemsRes, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					final String id_s = String.valueOf(((TextView)v.getTag()).getText());
					long[] id_l = {Long.valueOf(id_s).longValue()};
					listener.onItemClick(id_s, id_l[0], which);
				}
			})
			.show();
		}
    }
	/**
	 * ItemMenu点击监听接口（非多选模式）
	 * @param listener ItemMenuDialog对象
	 */
	public void setItemMenuDialog(ItemMenuDialog listener)
	{
		this.listener = listener;
	}
	public interface ItemMenuDialog
	{
		/**
		 * 设置ItemMenuDialogTitle资源(R.string.*)
		 * @return
		 */
		public int setItemMenuDialogTitleRes();
		/**
		 * 设置ItemMenuDialogItems资源（R.array.*)
		 * @return
		 */
		public int setItemMenuDialogItemsRes();
		/**
		 * 选择Item时调用的方法
		 * @param id_s String类型的id
		 * @param id_l long类型的id
		 * @param which
		 */
		public void onItemClick(String id_s, long id_l, int which);
	}
}
