package com.bleyl.recurrence.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bleyl.recurrence.BuildConfig;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.adapters.ContributionAdapter;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView versionText = (TextView) findViewById(R.id.version);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        versionText.setText(getResources().getString(R.string.version) + " " + BuildConfig.VERSION_NAME);
    }

    public void launchEmail(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getResources().getString(R.string.email)});
        this.startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_email)));
    }

    public void launchAppURL(View view) {
        String url = getResources().getString(R.string.app_url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    public void showLibrariesDialog(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_libraries, linearLayout, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.libraries));
        builder.setView(dialogView);
        builder.setPositiveButton(getResources().getString(R.string.ok), null);
        builder.show();

        dialogView.findViewById(R.id.tabLink).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getResources().getString(R.string.tabLink);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    public void showContributorsDialog(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.root);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_contributors, linearLayout, false);

        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.contributors_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        String[] contributorNames = getResources().getStringArray(R.array.contributors_array);
        String[] contributionTypes = getResources().getStringArray(R.array.contribution_array);
        recyclerView.setAdapter(new ContributionAdapter(this, R.layout.item_contributor_list, contributorNames, contributionTypes));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(getResources().getString(R.string.thanks_to));
        builder.setView(dialogView);
        builder.setPositiveButton(getResources().getString(R.string.ok), null);
        builder.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}