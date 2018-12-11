package  br.com.ufpe.cin.myfootprints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final FriendSharedLocationParser parser;

    public FriendAdapter(Context context, String[] values) {
        super(context, 0, values);
        this.context = context;
        this.values = values;
        this.parser = new FriendSharedLocationParser();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.friend_layout, parent, false);

        FriendSharedLocation location = this.parser.locationUpdatesFromSMSText(values[position]);

        TextView textView = (TextView) rowView.findViewById(R.id.txtitem);
        textView.setText(location.getFriendContactNumber());

        return rowView;
    }

    @Override
    public int getCount(){
        return values.length;
    }

}