/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.recorder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;


public class DefaultRecorder implements IExecutionRecorder {
    private final Stack<SubAction> actionStack = new Stack<>();

    public DefaultRecorder() {
    }

    @Override
    public String toReadableString() {
        return actionStack.get(0).toString();
    }

    @Override
    public void entryRDG(String rdg) {
        SubAction nowAction = null;
        if (!actionStack.isEmpty()) {
            nowAction = actionStack.peek();
        }
        SubAction newAction = new SubAction(rdg);
        if (null != nowAction) {
            nowAction.subActionList.add(newAction);
        }
        actionStack.push(newAction);
    }

    @Override
    public void leaveRDG() {
        actionStack.pop();
    }

    @Override
    public void stageResult(String stage, long result) {
        SubAction nowAction = actionStack.peek();
        nowAction.subActionList.add(new SampleAction(stage, result));
    }

    @Override
    public void stageResultWithDesc(String stage, long result, String finishDescribe) {
        SubAction nowAction = actionStack.peek();
        nowAction.subActionList.add(new SampleAction(stage, result, finishDescribe));
    }

    public abstract static class AbstractAction {
        protected final long time;

        protected AbstractAction(long time) {
            this.time = time;
        }
    }

    public static class SampleAction extends AbstractAction {

        private final String describe;
        private final Long   num;

        private final String finishDescribe;

        public SampleAction(String describe, long num) {
            super(System.currentTimeMillis());
            this.describe = describe;
            this.num = num;
            this.finishDescribe = null;
        }

        public SampleAction(String describe, long num, String finishDescribe) {
            super(System.currentTimeMillis());
            this.describe = describe;
            this.num = num;
            this.finishDescribe = finishDescribe;
        }

        public SampleAction(String describe, Long num, long time) {
            super(time);
            this.describe = describe;
            this.num = num;
            this.finishDescribe = null;
        }

        @Override
        public String toString() {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            String prefix = null == num ? "SUBQUERY" : "NUM=" + num;
            return prefix + ", " + describe + " @" + formatter.format(new Date(this.time));
        }

    }

    public static class SubAction extends AbstractAction {
        private final String               describe;
        private final List<AbstractAction> subActionList = new ArrayList<>();

        public SubAction(String describe) {
            super(System.currentTimeMillis());
            this.describe = describe;
        }

        @Override
        public String toString() {
            List<String> printLineList = new ArrayList<>();
            getPrettyLines(printLineList, "", this, true);

            StringBuilder sb = new StringBuilder();
            for (String line : printLineList) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }

        private void getPrettyLines(List<String> printLines, String prefix, AbstractAction action, boolean last) {
            if (action instanceof SampleAction) {
                if (last) {
                    printLines.add(prefix + "└─" + action);
                } else {
                    printLines.add(prefix + "├─" + action);
                }
            } else {
                SubAction subAction = (SubAction) action;
                String finishDescribe = "";
                if (!subAction.subActionList.isEmpty()) {
                    AbstractAction finish = subAction.subActionList.get(subAction.subActionList.size() - 1);
                    if (finish instanceof SampleAction) {
                        SampleAction finishSampleAction = (SampleAction) finish;
                        finishDescribe = finishSampleAction.finishDescribe;
                    }
                }
                getPrettyLines(printLines, prefix, new SampleAction(finishDescribe, null, subAction.time), last);

                String newPrefix;
                if (last) {
                    newPrefix = prefix + "    ";
                } else {
                    newPrefix = prefix + "│   ";
                }
                for (int i = 0; i < subAction.subActionList.size(); ++i) {
                    boolean isLast = i + 1 == subAction.subActionList.size();
                    AbstractAction aa = subAction.subActionList.get(i);
                    getPrettyLines(printLines, newPrefix, aa, isLast);
                }
            }
        }
    }

}