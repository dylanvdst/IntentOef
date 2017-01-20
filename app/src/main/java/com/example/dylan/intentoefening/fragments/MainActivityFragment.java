package com.example.dylan.intentoefening.fragments;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dylan.intentoefening.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class MainActivityFragment extends Fragment
{


    @BindView(R.id.btnSpeek)
    Button btnSpeak;
    @BindView(R.id.txvOutput)
    TextView txvOutput;
    @BindView(R.id.btnContacts)
    Button btnContacts;
    @BindView(R.id.btnDialer)
    Button btnDialer;
    @BindView(R.id.btnGoogle)
    Button btnGoogle;
    @BindView(R.id.btnWeb)
    Button btnWeb;
    @BindView(R.id.etGoogle)
    EditText etGoogle;
    @BindView(R.id.etWebURL)
    EditText etWebUrl;
    @BindView(R.id.tvContact)
    TextView tvContact;

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int REQ_CODE_CONTACTS = 101 ;

    public MainActivityFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.btnSpeek)
    public void startSpeech()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(getActivity(), getString(R.string.not_available), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btnWeb)
    public void btnWebClick()
    {
        goToWebURL();
    }

    private void goToWebURL()
    {
        try
        {
            if(etWebUrl.getText().equals(null))
            {
                Toast.makeText(getActivity(), R.string.no_url, Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(etWebUrl.getText().toString()));
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(getActivity(), R.string.no_webbrowser, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btnGoogle)
    public void btnGoogleClick()
    {
        googleSearch();
    }

    private void googleSearch()
    {
        try
        {
            if(etGoogle.getText().equals(null))
            {
                Toast.makeText(getActivity(), R.string.no_url, Toast.LENGTH_LONG).show();
            }
            else
            {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String word = etGoogle.getText().toString();
                intent.putExtra(SearchManager.QUERY, word);
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(getActivity(), R.string.no_webbrowser, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnDialer)
    public void btnDialerClick()
    {
        dialer();
    }

    private void dialer()
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            startActivity(intent);
        }
        catch (ActivityNotFoundException e)
        {
            Toast.makeText(getActivity(), R.string.no_support, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btnContacts)
    public void btnContactsClick()
    {
        contacts();
    }

    private void contacts()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_CONTACTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
        } else
        {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode)
            {
                case REQ_CODE_SPEECH_INPUT:
                {
                    if (resultCode == RESULT_OK && null != data)
                    {
                        ArrayList<String> resultSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        String zin = resultSTT.get(0);
                        switch (zin){
                            case "go to contacts": contacts(); break;
                            case "dial": dialer();break;
                            case "search Google": googleSearch(); break;
                            case "browse web": goToWebURL(); break;
                            default: txvOutput.setText(zin); break;

                        }
                        txvOutput.setText(zin);
                    }
                    break;
                }
                case REQ_CODE_CONTACTS:
                {
                    if (resultCode == RESULT_OK)
                    {
                        Uri contactData = data.getData();
                        Cursor c = getActivity().managedQuery(contactData, null, null, null, null);
                        if(c.moveToFirst())
                        {
                            tvContact.setText(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        }

                    }
                }
            }
        }

    }


}
