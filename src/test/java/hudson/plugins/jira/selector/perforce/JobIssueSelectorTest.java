package hudson.plugins.jira.selector.perforce;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import hudson.model.*;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Sets;

import hudson.plugins.jira.JiraCarryOverAction;
import hudson.plugins.jira.JiraSite;
import hudson.scm.ChangeLogSet;


public abstract class JobIssueSelectorTest {


    protected abstract JobIssueSelector createJobIssueSelector(); 
    @Test
    public void testFindsIssuesWithJiraParameters() {
        FreeStyleBuild build = mock(FreeStyleBuild.class);
        ChangeLogSet changeLogSet = mock(ChangeLogSet.class);
        BuildListener listener = mock(BuildListener.class);
        JiraSite jiraSite = mock(JiraSite.class);
        StringParameterValue parameter = mock(StringParameterValue.class);
        StringParameterValue parameterTwo = mock(StringParameterValue.class);
        ParametersAction action = mock(ParametersAction.class);
        List<ParameterValue> parameters = new ArrayList<ParameterValue>();

        when(listener.getLogger()).thenReturn(System.out);
        when(changeLogSet.iterator()).thenReturn(Collections.EMPTY_LIST.iterator());
        when(build.getChangeSet()).thenReturn(changeLogSet);
        when(build.getAction(ParametersAction.class)).thenReturn(action);
        when(action.getParameters()).thenReturn(parameters);
        when(parameter.getValue()).thenReturn("JIRA-123");
        when(parameterTwo.getValue()).thenReturn("JIRA-321");

        Set<String> ids;

        JobIssueSelector jobIssueSelector = createJobIssueSelector();
        // Initial state contains zero parameters
        ids = jobIssueSelector.findIssueIds(build, jiraSite, listener);
        Assert.assertTrue(ids.isEmpty());


        parameters.add(parameter);
        ids = jobIssueSelector.findIssueIds(build, jiraSite, listener);
        Assert.assertEquals(1, ids.size());
        Assert.assertEquals("JIRA-123", ids.iterator().next());

        parameters.add(parameterTwo);
        ids = jobIssueSelector.findIssueIds(build, jiraSite, listener);
        Assert.assertEquals(2, ids.size());
        Set<String> expected = Sets.newTreeSet(Sets.newHashSet("JIRA-123", "JIRA-321"));
        Assert.assertEquals(expected, ids);
    }
    @Test
    public void testFindsCarriedOnIssues() {
        
        FreeStyleBuild build = mock(FreeStyleBuild.class);
        FreeStyleBuild previousBuild = mock(FreeStyleBuild.class);
        ArrayList<String> issues = new ArrayList<String>();
        issues.add("GC-131");
        JiraCarryOverAction jiraCarryOverAction = mock(JiraCarryOverAction.class);
        when (build.getPreviousBuild()).thenReturn(previousBuild);
        when (previousBuild.getAction(JiraCarryOverAction.class)).thenReturn(jiraCarryOverAction);
        when (jiraCarryOverAction.getIDs()).thenReturn(issues);
        
        ChangeLogSet changeLogSet = mock(ChangeLogSet.class);
        BuildListener listener = mock(BuildListener.class);
        JiraSite jiraSite = mock(JiraSite.class);
       

        when(listener.getLogger()).thenReturn(System.out);
        when(changeLogSet.iterator()).thenReturn(Collections.EMPTY_LIST.iterator());
        when(build.getChangeSet()).thenReturn(changeLogSet);

        Set<String> ids;

        JobIssueSelector jobIssueSelector = createJobIssueSelector();

        ids = jobIssueSelector.findIssueIds(build, jiraSite, listener);
        Assert.assertEquals(1, ids.size());
        Set<String> expected = Sets.newTreeSet(Sets.newHashSet("GC-131"));
        Assert.assertEquals(expected, ids);
    }
}
