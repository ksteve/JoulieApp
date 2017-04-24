package com.example.kyle.joulieapp.Contracts;

import android.support.annotation.NonNull;

import com.example.kyle.joulieapp.Base.BasePresenter;
import com.example.kyle.joulieapp.Base.BaseView;
import com.example.kyle.joulieapp.Models.Rule;

import java.util.Collection;
import java.util.List;

/**
 * Created by Kyle on 2017-04-16.
 */

public interface RuleContract {

    interface View extends BaseView<RuleContract.Presenter> {

        void setLoadingIndicator(boolean active);

        void showRules(List<Rule> rules);

        void showAddRule();

        void showRuleRemoved(String ruleID);

        void updateRemoveRulesButton(boolean show);

        void updateRulePowerButton(boolean state, String message);

        void showRuleDetailsUi(String ruleId);

        void showNoRules();

        void refreshList();

        void showRequestFailed(String message);

        void showRequestSuccess(String message);
    }

    interface Presenter extends BasePresenter {

        void deleteRule(@NonNull Rule rule);

        void deleteRules(@NonNull Collection<Rule> rules);

        void loadRules(boolean forceUpdate);

        void showRemoveRules(boolean show);

        void openRuleDetails(@NonNull Rule rule);

        void addNewRule(@NonNull Rule rule);

        void toggleRulePower(@NonNull Rule rule, final boolean state);

    }
}
