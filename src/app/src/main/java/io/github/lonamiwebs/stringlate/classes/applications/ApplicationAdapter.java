package io.github.lonamiwebs.stringlate.classes.applications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.lonamiwebs.stringlate.classes.lazyloader.ImageLoader;
import io.github.lonamiwebs.stringlate.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ApplicationAdapter extends ArrayAdapter<Application> {
    private final ImageLoader mImageLoader;

    public ApplicationAdapter(Context context, List<Application> apps,
                              boolean allowInternetDownload) {
        super(context, R.layout.item_application_list, apps);
        mImageLoader = new ImageLoader(context, allowInternetDownload);
    }

    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Application app = getItem(position);

        // This may be the first time we use the recycled view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_application_list, parent, false);

        ImageView iconView = (ImageView)convertView.findViewById(R.id.appIcon);
        if (app.isInstalled()) {
            mImageLoader.loadImageAsync(iconView, app.getIconUrl(getContext()), app.getPackageName());
        } else {
            mImageLoader.loadImageAsync(iconView, app.getIconUrl(getContext()), null);
        }

        ((TextView)convertView.findViewById(R.id.appName)).setText(app.getName());
        ((TextView)convertView.findViewById(R.id.appDescription)).setText(app.getDescription());

        int visibility = app.isInstalled() ? VISIBLE : GONE;
        convertView.findViewById(R.id.installIndicatorView).setVisibility(visibility);

        return convertView;
    }
}
