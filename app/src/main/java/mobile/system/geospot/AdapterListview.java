package mobile.system.geospot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by giulio on 04/06/16.
 */
public class AdapterListview extends ArrayAdapter<PoiAdv> {
    private final Context context;

    public AdapterListview(Context context,int textViewResourceId, ArrayList<PoiAdv> PoiAdvs) {
        super(context, textViewResourceId,PoiAdvs);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row, parent, false);

        TextView firstLine = (TextView) rowView.findViewById(R.id.firstLine);
        TextView secondLine = (TextView) rowView.findViewById(R.id.secondLine);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);

        firstLine.setText(getItem(position).getName());
        secondLine.setText(getItem(position).getDescription());

       if(getItem(position).isPoiImage()){
           imageView.setImageResource(R.drawable.poi);
        } else {
           imageView.setImageResource(R.drawable.cart);
        }

        return rowView;
        }
}
