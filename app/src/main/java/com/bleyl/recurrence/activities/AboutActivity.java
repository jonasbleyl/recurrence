package com.bleyl.recurrence.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bleyl.recurrence.BuildConfig;
import com.bleyl.recurrence.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version) TextView versionText;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.root) LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        if (getActionBar() != null) getActionBar().setDisplayHomeAsUpEnabled(true);

        versionText.setText(BuildConfig.VERSION_NAME);
    }

    public void launchEmail(View view) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
        startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
    }

    public void launchAppURL(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.app_url)));
        startActivity(intent);
    }

    public void showLibrariesDialog(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_libraries, linearLayout, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(R.string.libraries);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, null);
        builder.show();

        dialogView.findViewById(R.id.tab_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.tab_link)));
                startActivity(intent);
            }
        });

        dialogView.findViewById(R.id.butter_knife_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.butter_knife_link)));
                startActivity(intent);
            }
        });

        dialogView.findViewById(R.id.material_dialogs_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.material_dialogs_link)));
                startActivity(intent);
            }
        });
    }

    public void showContributorsDialog(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.view_dialog_contributors, linearLayout, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(R.string.thanks_to);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.ok, null);
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