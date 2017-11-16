package com.example.bublikkasirin.textwatcher;


import android.text.Editable;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;

public class TimeWatcher implements android.text.TextWatcher {


    private final WeakReference<EditText> editTimeWeakReference;

    static private boolean addPoints = false;
    static private boolean addZero = false;

    TimeWatcher(EditText editText) {editTimeWeakReference = new WeakReference<EditText>(editText);}

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {

        /*

         * @origin      - time from user
         * @time        - two parts of the time ( hh:mm )
         * @iOrigin     - length of the @origin
         * @addPoints   - Boolean flag of adding ":" to the time
         * @addZero     - Boolean flag of adding "0" to the front

         */


        EditText editText = editTimeWeakReference.get();
        editText.removeTextChangedListener(this);
        String origin = editable.toString();
        int index = origin.indexOf(":");
        if (index != -1 && index != origin.length() - 1)
                origin = origin.substring(0,index+1) + origin.substring(index+1,origin.length()).replaceAll(":","");
        int iOrigin = origin.length();
        String[] time = origin.split(":");

        if (iOrigin != 0) {

            // If first char is ":".
            // Clean the string if this is the only symbol
            // Delete this symbol if it is on the 1st place
            if (origin.charAt(0) == ':') {
                if (iOrigin != 1) {
                    origin = origin.substring(1, origin.length());
                    iOrigin = origin.length();
                    time = origin.split(":");
                } else {
                    origin = "";
                    iOrigin = 0;
                    addPoints = false;
                    addZero = false;
                }
            }
            // If ":" was added by user.
            // If length is 2 it means that there will be only one char ( 0..9 )
            // Adds 0 in the front
            if (iOrigin == 2 && origin.charAt(origin.length() -1) == ':' && !addPoints) {
                origin = "0" + origin;
                iOrigin = origin.length();
                time = origin.split(":");
            }
        }

        // If the left part contains more than 2 chars
        if (time.length == 1 && time[0].length() > 2) {

            // If there is 3 chars. Move the last one to the right part
            if (time[0].length() == 3){
                origin = time[0].substring(0,2) + ":" + time[0].charAt(2);
                time = origin.split(":");
                iOrigin = origin.length();
                addPoints = true;
            }

            // if it is more than 3 chars ( 4 expected ) then div on two parts
            if (time[0].length() > 3){
                origin = time[0].substring(0,2) + ":" + time[0].substring(2,4);
                time = origin.split(":");
                iOrigin = origin.length();
                addPoints = true;
            }
        }

        if (time.length == 2) {

            // To prevent adding zeroes if the user decides to delete some symbols
            if (iOrigin == 4 && addZero) {
                if (time[1].equals("0")) {
                    origin = time[0]+":";
                    iOrigin = origin.length();
                    time = origin.split(":");
                    addPoints = true;
                    addZero = false;
                }
            }
            // if there is less than 5 symbols but ":" presents
            // and this is not deleting iteration
            // then normalize by adding 0 in the front
            else if (iOrigin < 5) {
                if (time[0].length() == 1) {
                    time[0] = "0" + time[0];
                }
                if (time[1].length() == 1) {
                    time[1] = "0" + time[1];
                }
                origin = time[0] + ":" + time[1];
                iOrigin = origin.length();
                addPoints = true;
                addZero = true;
            }
        }

        // If user adds more than one ":"
        // Deleting them and rebuilding the origin string
        if (time.length > 2) {
            StringBuilder originBuilder = new StringBuilder();
            addPoints = false;
            for (String aTime : time) {
                originBuilder.append(aTime);
            }

            origin = originBuilder.toString();
            iOrigin = origin.length();

            // If resulting string has more than 3 symbols div it on two parts
            if (iOrigin > 3) {
                origin = origin.substring(0,2) + ":" + origin.substring(2,4);
                iOrigin = origin.length();
                addPoints = true;
            }
        }

        // If len of the string is 3 then automatically add ":" to the end
        if (iOrigin == 3 && origin.indexOf(':') == -1 && !addPoints) {
            origin = origin.substring(0,2) + ':' + origin.charAt(2);
            addPoints = true;
        } else
            if (iOrigin == 2 && origin.indexOf(':') == -1 && addPoints){
                addPoints = false;
        }
        if (iOrigin == 3) addZero = false;

        // If len is 4 to prevent errors like "12:1" adds 0 to the end
        if (iOrigin == 4) {
            if (origin.charAt(iOrigin-2) == ':' && !addZero){
                origin += '0';
                addZero = true;
            }
        }

        // The resulting branch where numbers are normalizing
        // If user adds new symbol to the minutes that have already filled up
        // New symbol changes the last one
        else if (iOrigin >= 5) {
                if (iOrigin > 5) {
                    if (time[0].length() > 2) {
                        time[0] = time[0].substring(0, 2);
                    }
                    if (time[1].length() > 2) {
                        if (time[1].charAt(0) == '0') {
                            time[1] = time[1].charAt(1) + "" + time[1].charAt(2);
                        }
                        if (time[1].charAt(0) != '0') {
                            time[1] = time[1].charAt(0) + "" + time[1].charAt(time[1].length() - 1);
                        }
                    }
                    origin = time[0] + ':' + time[1];
                    iOrigin = origin.length();
                }
                if (iOrigin == 5) {

                    // To prevent error like "0:006" causing by deleting symbols in different order
                    // Normalizing time making the length of the parts equals 2
                    if (time[0].length() - time[1].length() != 0) {
                        if (time[1].length() == 3) {
                            time[0] = time[0] + time[1].charAt(0);
                            time[1] = time[1].substring(1,3);
                        }
                        if (time[0].length() == 3) {
                            time[1] = time[0].charAt(2) + time[1];
                            time[0] = time[0].substring(0,2);
                        }
                    }
                    int hours = Integer.parseInt(time[0]);
                    int minutes = Integer.parseInt(time[1]);

                    // If there is incorrect time then put max instead
                    time[0] = hours > 23 ? "23" : time[0];
                    time[1] = minutes > 59 ? "59" : time[1];

                    origin = time[0] + ":" + time[1];

                }
            }

        iOrigin = origin.length();

        editText.setText(origin);
        editText.setSelection(iOrigin);
        editText.addTextChangedListener(this);
    }
}
