package ru.handh.doctor.ui;

import android.support.v4.app.Fragment;
import android.widget.ViewFlipper;

import ru.handh.doctor.utils.Constants;

/**
 * Created by sgirn on 19.11.2015.
 * родитель всех фрагментов
 */
public class ParentFragment extends Fragment {

    protected ViewFlipper viewFlipper;


    public void showData() {
        if (viewFlipper.getDisplayedChild() != Constants.VIEW_CONTENT)
            viewFlipper.setDisplayedChild(Constants.VIEW_CONTENT);
    }

    public void showProgress() {
        if (viewFlipper.getDisplayedChild() != Constants.VIEW_PROGRESS)
            viewFlipper.setDisplayedChild(Constants.VIEW_PROGRESS);
    }

    public void showError() {
        if (viewFlipper.getDisplayedChild() != Constants.VIEW_ERROR)
            viewFlipper.setDisplayedChild(Constants.VIEW_ERROR);
    }
}
