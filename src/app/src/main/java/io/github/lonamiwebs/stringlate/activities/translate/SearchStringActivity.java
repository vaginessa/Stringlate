package io.github.lonamiwebs.stringlate.activities.translate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import io.github.lonamiwebs.stringlate.R;
import io.github.lonamiwebs.stringlate.classes.LocaleString;
import io.github.lonamiwebs.stringlate.classes.resources.ResourcesTranslation;
import io.github.lonamiwebs.stringlate.classes.resources.ResourcesTranslationAdapter;
import io.github.lonamiwebs.stringlate.classes.repos.RepoHandler;

import static io.github.lonamiwebs.stringlate.utilities.Constants.EXTRA_LOCALE;
import static io.github.lonamiwebs.stringlate.utilities.Constants.EXTRA_REPO;

public class SearchStringActivity extends AppCompatActivity {

    //region Members

    private RepoHandler mRepo;
    private String mLocale;

    private EditText mSearchEditText;
    private ListView mResourcesListView;

    //endregion

    //region Initialization

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_string);

        mSearchEditText = (EditText)findViewById(R.id.searchEditText);
        mResourcesListView = (ListView)findViewById(R.id.resourcesListView);
        final ImageButton clearFilterButton = (ImageButton)findViewById(R.id.clearFilterButton);

        Intent intent = getIntent();
        mRepo = RepoHandler.fromBundle(this, intent.getBundleExtra(EXTRA_REPO));
        mLocale = intent.getStringExtra(EXTRA_LOCALE);

        setTitle(String.format("%s/%s (%s)", mRepo.getName(false),
                LocaleString.getDisplay(mLocale), mLocale));

        refreshResourcesListView(null);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String search = charSequence.toString();
                refreshResourcesListView(search);
                if (search.isEmpty()) {
                    clearFilterButton.setImageResource(R.drawable.ic_search_36dp);
                } else {
                    clearFilterButton.setImageResource(R.drawable.ic_backspace_36dp);
                }
            }
        });

        mResourcesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Save the current search, although we also need to pass it
                // with the intent because the json is not reloaded from the
                // file when we're asked for the updated string (on Translate)
                final String filter = mSearchEditText.getText().toString();
                mRepo.setStringFilter(filter);

                // Return the selected string to the parent activity
                ResourcesTranslation rt = (ResourcesTranslation)mResourcesListView.getItemAtPosition(i);
                Intent data = new Intent();
                data.putExtra("id", rt.getId());
                data.putExtra("filter", filter);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        mSearchEditText.setText(mRepo.getStringFilter());
    }

    //endregion

    //region Button events

    public void onClearFilterClick(final View v) {
        mSearchEditText.setText("");
    }

    //endregion

    //region ListView refreshing

    private void refreshResourcesListView(String filter) {
        ArrayList<ResourcesTranslation> rts = ResourcesTranslation.fromPairs(
                mRepo.loadDefaultResources(), mRepo.loadResources(mLocale), filter);

        mResourcesListView.setAdapter(new ResourcesTranslationAdapter(this, rts));
    }

    //endregion
}
