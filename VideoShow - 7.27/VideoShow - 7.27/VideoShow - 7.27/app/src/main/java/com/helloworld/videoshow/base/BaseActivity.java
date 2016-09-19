package com.helloworld.videoshow.base;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.helloworld.videoshow.R;

/**
 * Created by Hello on 2016/7/7.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar toolbar;

    protected void setToolbarTitle(Toolbar toolbar, String title) {
        ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText(title);
        setTitle("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //send email to developer
    protected void feedback() {
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("plain/text");
        String[] emailReciver = {"hellofvd@yahoo.com"};
        String emailSubject = getString(R.string.feedback_subject);
        String emailBody = getString(R.string.feedback_body);
        email.putExtra(Intent.EXTRA_EMAIL, emailReciver);
        email.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        email.putExtra(Intent.EXTRA_TEXT, emailBody);
        startActivity(Intent.createChooser(email, getString(R.string.chose_email_client)));
    }
}
