package com.suzhou.concept.lil.data.remote.bean;

import java.util.List;

public class Exercise_concept {

    /**
     * MultipleChoice : [{"question":"____?\nYes, she is my daughter.","answer":"3","voa_id":"1003","choice_B":"Is your daughter","choice_C":"Is this your daughter ","choice_D":"Is it this your daughter","index_id":"1","choice_A":"Is it your daughter"},{"question":"Is this your coat?____.","answer":"3","voa_id":"1003","choice_B":"No, it is ","choice_C":"No, it isn't","choice_D":"NO, it's","index_id":"2","choice_A":"Yes, it isn't"},{"question":"____. Thank you,sir. ","answer":"1","voa_id":"1003","choice_B":"Here's is your ticket","choice_C":"Your ticket is here","choice_D":"Your ticket here is","index_id":"3","choice_A":"Here's your ticket"},{"question":"This is John speaking. who is that?\n____.","answer":"2","voa_id":"1003","choice_B":"This is Kate ","choice_C":"I'm your friend","choice_D":"This is Kate's speaking","index_id":"4","choice_A":"I am Kate"},{"question":"Mum,I'm thirsty(口渴). would you please give me some ____ ?","answer":"3","voa_id":"1003","choice_B":"cake","choice_C":"water","choice_D":"books","index_id":"5","choice_A":"pencils"}]
     * VoaStructureExercise : [{"number":"1","note":"This is my shirt.(变为一般疑问句)","ques_num":"0","answer":"Is this your shirt?","desc_CH":"按要求改写下列句子","column":"1","id":"1003","desc_EN":"Rewrite these sentences","type":"0"},{"number":"2","note":"This is not her teacher.(变为肯定句)","ques_num":"0","answer":"This is her teacher.","desc_CH":"","column":"2","id":"1003","desc_EN":"","type":"0"},{"number":"3","note":"This is my umbrella.(变为否定句)","ques_num":"0","answer":"This is not my umbrella.","desc_CH":"","column":"3","id":"1003","desc_EN":"","type":"0"},{"number":"4","note":"Whose skirt is this?(回答：这是我的短裙)","ques_num":"0","answer":"This is my skirt.","desc_CH":"","column":"4","id":"1003","desc_EN":"","type":"0"},{"number":"5","note":"Is this his pencil?(变为否定句)","ques_num":"0","answer":"This isn't his pencil.","desc_CH":"","column":"5","id":"1003","desc_EN":"","type":"0"}]
     * SizeVoaDiffcultyExercise : 0
     * SizeVoaStructureExercise : 5
     * SizeMultipleChoice : 5
     * VoaDiffcultyExercise : []
     */

    private Integer SizeVoaDiffcultyExercise;
    private Integer SizeVoaStructureExercise;
    private Integer SizeMultipleChoice;
    private List<MultipleChoiceDTO> MultipleChoice;
    private List<VoaStructureExerciseDTO> VoaStructureExercise;
    private List<?> VoaDiffcultyExercise;

    public Integer getSizeVoaDiffcultyExercise() {
        return SizeVoaDiffcultyExercise;
    }

    public void setSizeVoaDiffcultyExercise(Integer SizeVoaDiffcultyExercise) {
        this.SizeVoaDiffcultyExercise = SizeVoaDiffcultyExercise;
    }

    public Integer getSizeVoaStructureExercise() {
        return SizeVoaStructureExercise;
    }

    public void setSizeVoaStructureExercise(Integer SizeVoaStructureExercise) {
        this.SizeVoaStructureExercise = SizeVoaStructureExercise;
    }

    public Integer getSizeMultipleChoice() {
        return SizeMultipleChoice;
    }

    public void setSizeMultipleChoice(Integer SizeMultipleChoice) {
        this.SizeMultipleChoice = SizeMultipleChoice;
    }

    public List<MultipleChoiceDTO> getMultipleChoice() {
        return MultipleChoice;
    }

    public void setMultipleChoice(List<MultipleChoiceDTO> MultipleChoice) {
        this.MultipleChoice = MultipleChoice;
    }

    public List<VoaStructureExerciseDTO> getVoaStructureExercise() {
        return VoaStructureExercise;
    }

    public void setVoaStructureExercise(List<VoaStructureExerciseDTO> VoaStructureExercise) {
        this.VoaStructureExercise = VoaStructureExercise;
    }

    public List<?> getVoaDiffcultyExercise() {
        return VoaDiffcultyExercise;
    }

    public void setVoaDiffcultyExercise(List<?> VoaDiffcultyExercise) {
        this.VoaDiffcultyExercise = VoaDiffcultyExercise;
    }

    public static class MultipleChoiceDTO {
        /**
         * question : ____?
         Yes, she is my daughter.
         * answer : 3
         * voa_id : 1003
         * choice_B : Is your daughter
         * choice_C : Is this your daughter
         * choice_D : Is it this your daughter
         * index_id : 1
         * choice_A : Is it your daughter
         */

        private String question;
        private String answer;
        private String voa_id;
        private String choice_B;
        private String choice_C;
        private String choice_D;
        private String index_id;
        private String choice_A;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getVoa_id() {
            return voa_id;
        }

        public void setVoa_id(String voa_id) {
            this.voa_id = voa_id;
        }

        public String getChoice_B() {
            return choice_B;
        }

        public void setChoice_B(String choice_B) {
            this.choice_B = choice_B;
        }

        public String getChoice_C() {
            return choice_C;
        }

        public void setChoice_C(String choice_C) {
            this.choice_C = choice_C;
        }

        public String getChoice_D() {
            return choice_D;
        }

        public void setChoice_D(String choice_D) {
            this.choice_D = choice_D;
        }

        public String getIndex_id() {
            return index_id;
        }

        public void setIndex_id(String index_id) {
            this.index_id = index_id;
        }

        public String getChoice_A() {
            return choice_A;
        }

        public void setChoice_A(String choice_A) {
            this.choice_A = choice_A;
        }
    }

    public static class VoaStructureExerciseDTO {
        /**
         * number : 1
         * note : This is my shirt.(变为一般疑问句)
         * ques_num : 0
         * answer : Is this your shirt?
         * desc_CH : 按要求改写下列句子
         * column : 1
         * id : 1003
         * desc_EN : Rewrite these sentences
         * type : 0
         */

        private String number;
        private String note;
        private String ques_num;
        private String answer;
        private String desc_CH;
        private String column;
        private String id;
        private String desc_EN;
        private String type;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getQues_num() {
            return ques_num;
        }

        public void setQues_num(String ques_num) {
            this.ques_num = ques_num;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public String getDesc_CH() {
            return desc_CH;
        }

        public void setDesc_CH(String desc_CH) {
            this.desc_CH = desc_CH;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDesc_EN() {
            return desc_EN;
        }

        public void setDesc_EN(String desc_EN) {
            this.desc_EN = desc_EN;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
