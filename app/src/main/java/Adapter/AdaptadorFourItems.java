package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facturas.julian.mobilefacturas.R;

import java.util.ArrayList;

/**
 * Created by JULIANEDUARDO on 10/02/2015.
 */
public class AdaptadorFourItems extends BaseAdapter{
    protected Context context;
    protected ArrayList<DetalleFourItems> items;
    protected ArrayList<DetalleFourItems> itemsBackup;
    LayoutInflater  inflater;


    public AdaptadorFourItems(Context _context, ArrayList<DetalleFourItems> _items){
        this.context        = _context;
        this.itemsBackup    = new ArrayList<DetalleFourItems>();
        this.items          = new ArrayList<DetalleFourItems>();

        this.itemsBackup.addAll(_items);
        this.items.addAll(_items);
        inflater            = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }



    public DetalleFourItems getDataOnFilter(int _position){
        return this.items.get(_position);
    }

    public void Filtrar(int _campo, String _txtFiltro){
        this.items.clear();
        if(_txtFiltro.length() == 0 ){
            this.items.addAll(this.itemsBackup);
        }else{
            String filterString = _txtFiltro.toLowerCase();
            String filterCampo  = "";
            for(int i = 0; i<this.itemsBackup.size(); i++){
                switch(_campo){
                    case 0:
                        filterCampo = this.itemsBackup.get(i).getItem1().toLowerCase();
                        break;

                    case 1:
                        filterCampo = this.itemsBackup.get(i).getItem2().toLowerCase();
                        break;

                    case 2:
                        filterCampo = this.itemsBackup.get(i).getItem3().toLowerCase();
                        break;

                    case 3:
                        filterCampo = this.itemsBackup.get(i).getItem4().toLowerCase();
                        break;

                    default:
                        break;
                }
                if(filterCampo.contains(filterString)){
                    this.items.add(this.itemsBackup.get(i));
                }
            }
        }
        //notifyDataSetChanged();
        notifyDataSetChanged();
    }


/*********************************Finalizacion del Filter********************************/


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View v = convertView;
        final ViewHolder holder;
        if(convertView == null){
            holder  = new ViewHolder();
            convertView = inflater.inflate(R.layout.four_items_horizontal,null);
            holder.item1 = (TextView) convertView.findViewById(R.id.fourItem1);
            holder.item2 = (TextView) convertView.findViewById(R.id.fourItem2);
            holder.item3 = (TextView) convertView.findViewById(R.id.fourItem3);
            holder.item4 = (TextView) convertView.findViewById(R.id.fourItem4);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item1.setText(items.get(position).getItem1());
        holder.item2.setText(items.get(position).getItem2());
        holder.item3.setText(items.get(position).getItem3());
        holder.item4.setText(items.get(position).getItem4());
        return convertView;
    }

    public class ViewHolder{
        TextView    item1;
        TextView    item2;
        TextView    item3;
        TextView    item4;
    }
}
