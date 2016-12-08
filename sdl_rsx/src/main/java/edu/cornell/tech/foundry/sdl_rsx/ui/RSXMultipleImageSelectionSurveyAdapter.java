package edu.cornell.tech.foundry.sdl_rsx.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.ResUtils;
import org.researchstack.backbone.utils.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.cornell.tech.foundry.sdl_rsx.answerformat.RSXImageChoiceAnswerFormat;
import edu.cornell.tech.foundry.sdl_rsx.choice.RSXImageChoice;
import edu.cornell.tech.foundry.sdl_rsx.step.RSXMultipleImageSelectionSurveyStep;
import edu.cornell.tech.foundry.sdl_rsx.R;

/**
 * Created by jk on 6/15/16.
 */
public class RSXMultipleImageSelectionSurveyAdapter <T> extends BaseAdapter {

    private Set<T> currentSelected;
    private RSXImageChoice[] imageChoices;
    private RSXMultipleImageSelectionSurveyStep step;
    private int cellWidth;

    public RSXMultipleImageSelectionSurveyAdapter(
            RSXMultipleImageSelectionSurveyStep step,
            StepResult<T[]> result) {
        super();
        this.step = step;
        // Restore results
        currentSelected = new HashSet<>();

        T[] resultArray = result.getResult();
        if(resultArray != null && resultArray.length > 0)
        {
            currentSelected.addAll(Arrays.asList(resultArray));
        }

        RSXImageChoiceAnswerFormat answerFormat = (RSXImageChoiceAnswerFormat)this.getStep().getAnswerFormat();

        this.imageChoices = answerFormat.getImageChoices();
    }

    @Override
    public int getCount() {
        int length = this.imageChoices.length;
        return length;
    }

    protected RSXMultipleImageSelectionSurveyStep getStep() {
        return this.step;
    }

    @Override
    public Object getItem(int position) {
        return this.imageChoices[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    protected View configureCellForImageChoice(View missCell, RSXImageChoice<T> imageChoice, ViewGroup parent) {

        ImageView itemImageView = (ImageView) missCell.findViewById(R.id.item_image_view);
        ImageView checkImageView = (ImageView) missCell.findViewById(R.id.check_image_view);
        LinearLayout textContainer = (LinearLayout) missCell.findViewById(R.id.text_container);

        try {
            InputStream inputStream = missCell.getContext().getAssets().open(imageChoice.getNormalStateImage());
            Drawable d = Drawable.createFromStream(inputStream, null);
            itemImageView.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.getSelectedForValue((T) imageChoice.getValue())) {

            if(this.getStep().getOptions().getItemCellSelectedColor() != 0) {
                int color = this.getStep().getOptions().getItemCellSelectedColor();
                missCell.setBackgroundColor(color);
            }

            if (!TextUtils.isEmpty( this.getStep().getOptions().getItemCellSelectedOverlayImageTitle() )) {
                try {
                    InputStream inputStream = missCell.getContext().getAssets().open(this.getStep().getOptions().getItemCellSelectedOverlayImageTitle());
                    Drawable d = Drawable.createFromStream(inputStream, null);
                    checkImageView.setImageDrawable(d);
                    checkImageView.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            missCell.setBackgroundColor(missCell.getResources().getColor(android.R.color.transparent));
            checkImageView.setVisibility(View.INVISIBLE);
        }

        textContainer.setVisibility(View.GONE);

        return missCell;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FrameLayout cell;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (convertView == null) {
            cell = (FrameLayout) inflater.inflate(
                    R.layout.rsx_multiple_image_selection_survey_cell,
                    parent,
                    false
            );
        }
        else {
            cell = (FrameLayout) convertView;
        }

        return this.configureCellForImageChoice(cell, this.imageChoices[position], parent);
    }

    public Set<T> getCurrentSelected() {
        return this.currentSelected;
    }

    public void clearCurrentSelected() {
        this.currentSelected.clear();
    }

    public void setSelectedForValue(T value, boolean selected) {
        //add or remove from hash based on selected
        if (selected) {
            this.currentSelected.add(value);
        }
        else {
            this.currentSelected.remove(value);
        }
    }

    public boolean getSelectedForValue(T value) {
        return this.currentSelected.contains(value);
    }

}
