package com.example.formapp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.formapp.entity.Answer;
import com.example.formapp.entity.Choice;
import com.example.formapp.entity.Form;
import com.example.formapp.entity.Question;
import com.example.formapp.entity.Response;
import com.example.formapp.repository.AnswerRepository;
import com.example.formapp.repository.ChoiceRepository;
import com.example.formapp.repository.FormRepository;
import com.example.formapp.repository.QuestionRepository;
import com.example.formapp.repository.ResponseRepository;

@Controller
public class HomeController {

    private final FormRepository formRepository;
    private final QuestionRepository questionRepository;
    private final ChoiceRepository choiceRepository;
    private final ResponseRepository responseRepository;
    private final AnswerRepository answerRepository;

    public HomeController(
            FormRepository formRepository,
            QuestionRepository questionRepository,
            ChoiceRepository choiceRepository,
            ResponseRepository responseRepository,
            AnswerRepository answerRepository) {

        this.formRepository = formRepository;
        this.questionRepository = questionRepository;
        this.choiceRepository = choiceRepository;
        this.responseRepository = responseRepository;
        this.answerRepository = answerRepository;
    }


    /*
     * ==========================================
     * 選択肢を1つ保存
     * ==========================================
     */

    private void saveChoice(
            Question question,
            String choiceText) {

        if (choiceText == null
                || choiceText.trim().isEmpty()) {

            return;
        }

        Choice choice = new Choice();

        choice.setChoiceText(
                choiceText.trim()
        );

        choice.setQuestion(
                question
        );

        choiceRepository.save(
                choice
        );
    }


    /*
     * ==========================================
     * トップページ
     * ==========================================
     */

    @GetMapping("/")
    public String index(Model model) {

        model.addAttribute(
                "forms",
                formRepository.findAll()
        );

        return "index";
    }


    /*
     * ==========================================
     * フォーム作成画面
     * ==========================================
     */

    @GetMapping("/form/create")
    public String createForm() {

        return "form-create";
    }


    /*
     * ==========================================
     * フォーム作成
     * ==========================================
     */

    @PostMapping("/form/create")
    public String createForm(
            @RequestParam String title,
            @RequestParam String description) {

        Form form = new Form();

        form.setTitle(
                title
        );

        form.setDescription(
                description
        );

        form.setPublished(
                false
        );

        formRepository.save(
                form
        );

        return "redirect:/";
    }


    /*
     * ==========================================
     * フォーム編集画面
     *
     * フォーム名・説明を再編集する
     * ==========================================
     */

    @GetMapping("/form/{id}/edit")
    public String editFormPage(
            @PathVariable Long id,
            Model model) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        model.addAttribute(
                "form",
                form
        );

        return "form-edit";
    }


    /*
     * ==========================================
     * フォーム編集保存
     *
     * フォーム名・説明を更新する
     *
     * 公開状態は変更しない
     * ==========================================
     */

    @PostMapping("/form/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String description) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        /*
         * フォーム名を更新
         */

        form.setTitle(
                title
        );


        /*
         * 説明を更新
         */

        form.setDescription(
                description
        );


        /*
         * 保存
         */

        formRepository.save(
                form
        );


        /*
         * フォーム詳細へ戻る
         */

        return "redirect:/form/" + id;
    }


    /*
     * ==========================================
     * フォーム詳細
     * ==========================================
     */

    @GetMapping("/form/{id}")
    public String formDetail(
            @PathVariable Long id,
            Model model) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        model.addAttribute(
                "form",
                form
        );

        var questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );

        model.addAttribute(
                "questions",
                questions
        );

        Map<Long, List<Choice>> questionChoices =
                new HashMap<>();

        for (Question question : questions) {

            questionChoices.put(
                    question.getId(),
                    choiceRepository.findByQuestionId(
                            question.getId()
                    )
            );
        }

        model.addAttribute(
                "questionChoices",
                questionChoices
        );

        return "form-detail";
    }


    /*
     * ==========================================
     * 質問追加画面
     * ==========================================
     */

    @GetMapping("/form/{id}/question/create")
    public String createQuestion(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "formId",
                id
        );

        return "question-create";
    }


    /*
     * ==========================================
     * 質問追加
     *
     * choices を複数受け取る
     * ==========================================
     */

    @PostMapping("/form/{id}/question")
    public String createQuestion(
            @PathVariable Long id,
            @RequestParam String questionText,
            @RequestParam String questionType,
            @RequestParam(
                    required = false,
                    defaultValue = "false")
            boolean required,
            @RequestParam(
                    required = false)
            List<String> choices) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        Question question =
                new Question();

        question.setForm(
                form
        );

        question.setQuestionText(
                questionText
        );

        question.setQuestionType(
                questionType
        );

        question.setRequired(
                required
        );

        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );

        question.setQuestionOrder(
                questions.size() + 1
        );

        questionRepository.save(
                question
        );


        /*
         * ==========================================
         * 選択式の場合
         *
         * 送られてきた選択肢をすべて保存
         * ==========================================
         */

        if (questionType.equals("radio")
                || questionType.equals("checkbox")) {

            if (choices != null) {

                for (String choiceText : choices) {

                    saveChoice(
                            question,
                            choiceText
                    );
                }
            }
        }

        return "redirect:/form/" + id;
    }


    /*
     * ==========================================
     * 質問編集画面
     * ==========================================
     */

    @GetMapping("/form/{formId}/question/{questionId}/edit")
    public String editQuestionPage(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            Model model) {

        Form form =
                formRepository.findById(formId)
                        .orElseThrow();

        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow();


        /*
         * 別のフォームの質問を
         * 編集できないようにする
         */

        if (!question.getForm().getId()
                .equals(form.getId())) {

            throw new IllegalArgumentException(
                    "この質問は指定されたフォームに属していません。"
            );
        }


        List<Choice> choices =
                choiceRepository.findByQuestionId(
                        questionId
                );


        model.addAttribute(
                "form",
                form
        );

        model.addAttribute(
                "question",
                question
        );

        model.addAttribute(
                "choices",
                choices
        );

        return "question-edit";
    }


    /*
     * ==========================================
     * 質問編集保存
     *
     * choices を複数受け取る
     * ==========================================
     */

    @PostMapping("/form/{formId}/question/{questionId}/edit")
    public String editQuestion(
            @PathVariable Long formId,
            @PathVariable Long questionId,
            @RequestParam String questionText,
            @RequestParam String questionType,
            @RequestParam(
                    required = false,
                    defaultValue = "false")
            boolean required,
            @RequestParam(
                    required = false)
            List<String> choices) {

        Form form =
                formRepository.findById(formId)
                        .orElseThrow();

        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow();


        /*
         * 別のフォームの質問を
         * 編集できないようにする
         */

        if (!question.getForm().getId()
                .equals(form.getId())) {

            throw new IllegalArgumentException(
                    "この質問は指定されたフォームに属していません。"
            );
        }


        /*
         * ==========================================
         * ① 質問本体を更新
         * ==========================================
         */

        question.setQuestionText(
                questionText
        );

        question.setQuestionType(
                questionType
        );

        question.setRequired(
                required
        );

        questionRepository.save(
                question
        );


        /*
         * ==========================================
         * ② 既存の選択肢を削除
         *
         * 削除する前に分岐設定を解除する
         * ==========================================
         */

        List<Choice> oldChoices =
                choiceRepository.findByQuestionId(
                        questionId
                );

        for (Choice choice : oldChoices) {

            choice.setNextQuestion(
                    null
            );

            choiceRepository.save(
                    choice
            );
        }

        choiceRepository.deleteAll(
                oldChoices
        );


        /*
         * ==========================================
         * ③ 新しい選択肢を保存
         * ==========================================
         */

        if (questionType.equals("radio")
                || questionType.equals("checkbox")) {

            if (choices != null) {

                for (String choiceText : choices) {

                    saveChoice(
                            question,
                            choiceText
                    );
                }
            }
        }


        /*
         * フォーム詳細へ戻る
         */

        return "redirect:/form/" + formId;
    }


    /*
     * ==========================================
     * 回答画面
     * ==========================================
     */

    @GetMapping("/answer/{id}")
    public String answerForm(
            @PathVariable Long id,
            Model model) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        if (!form.isPublished()) {

            return "form-not-published";
        }


        var questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );


        Map<Long, List<Choice>> questionChoices =
                new HashMap<>();


        for (Question question : questions) {

            questionChoices.put(
                    question.getId(),
                    choiceRepository.findByQuestionId(
                            question.getId()
                    )
            );
        }


        model.addAttribute(
                "form",
                form
        );

        model.addAttribute(
                "questions",
                questions
        );

        model.addAttribute(
                "questionChoices",
                questionChoices
        );


        return "answer-form";
    }


    /*
     * ==========================================
     * 回答送信
     * ==========================================
     */

    @PostMapping("/answer/{id}")
    public String submitAnswer(
            @PathVariable Long id,
            @RequestParam MultiValueMap<String, String> params) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();


        Response response =
                new Response();

        response.setForm(
                form
        );

        responseRepository.save(
                response
        );


        /*
         * ==========================================
         * 質問一覧を取得
         * ==========================================
         */

        var questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );


        /*
         * ==========================================
         * 各質問の回答を保存
         * ==========================================
         */

        for (Question question : questions) {

            String key =
                    "question-" + question.getId();


            /*
             * ==========================================
             * チェックボックス
             *
             * 複数選択された回答を
             * 1つのAnswerにまとめる
             *
             * 例：
             *
             * 赤
             * 青
             *
             * ↓
             *
             * 赤,青
             * ==========================================
             */

            if (question.getQuestionType()
                    .equals("checkbox")) {

                List<String> selectedValues =
                        params.get(key);


                /*
                 * 回答がない場合
                 */

                if (selectedValues == null
                        || selectedValues.isEmpty()) {

                    continue;
                }


                /*
                 * 空の回答を除外
                 */

                List<String> validValues =
                        new ArrayList<>();


                for (String value :
                        selectedValues) {

                    if (value != null
                            && !value.isBlank()) {

                        validValues.add(
                                value.trim()
                        );
                    }
                }


                if (validValues.isEmpty()) {

                    continue;
                }


                /*
                 * カンマ区切りで保存
                 */

                String answerText =
                        String.join(
                                ",",
                                validValues
                        );


                Answer answer =
                        new Answer();

                answer.setResponse(
                        response
                );

                answer.setQuestion(
                        question
                );

                answer.setAnswerText(
                        answerText
                );

                answerRepository.save(
                        answer
                );


                continue;
            }


            /*
             * ==========================================
             * 記述式・ラジオボタン
             *
             * 複数値ではないので
             * getFirst() を使用
             * ==========================================
             */

            String answerText =
                    params.getFirst(key);


            if (answerText == null
                    || answerText.isBlank()) {

                continue;
            }


            Answer answer =
                    new Answer();

            answer.setResponse(
                    response
            );

            answer.setQuestion(
                    question
            );

            answer.setAnswerText(
                    answerText
            );

            answerRepository.save(
                    answer
            );
        }


        /*
         * ==========================================
         * 回答完了画面
         * ==========================================
         */

        return "answer-complete";
    }


    /*
     * ==========================================
     * フォーム公開
     * ==========================================
     */

    @PostMapping("/form/{id}/publish")
    public String publishForm(
            @PathVariable Long id) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        form.setPublished(
                true
        );

        formRepository.save(
                form
        );

        return "redirect:/form/" + id;
    }


    /*
     * ==========================================
     * フォーム非公開
     * ==========================================
     */

    @PostMapping("/form/{id}/unpublish")
    public String unpublishForm(
            @PathVariable Long id) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();

        form.setPublished(
                false
        );

        formRepository.save(
                form
        );

        return "redirect:/form/" + id;
    }


    /*
     * ==========================================
     * 回答一覧
     * ==========================================
     */

    @GetMapping("/form/{id}/responses")
    public String responses(
            @PathVariable Long id,
            Model model) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();


        List<Response> responses =
                responseRepository.findByFormId(
                        id
                );


        Map<Long, List<Answer>> responseAnswers =
                new HashMap<>();


        for (Response response : responses) {

            responseAnswers.put(
                    response.getId(),
                    answerRepository.findByResponseId(
                            response.getId()
                    )
            );
        }


        model.addAttribute(
                "form",
                form
        );

        model.addAttribute(
                "responses",
                responses
        );

        model.addAttribute(
                "responseAnswers",
                responseAnswers
        );


        return "responses";
    }


    /*
     * ==========================================
     * 回答統計
     * ==========================================
     */

    @GetMapping("/form/{id}/statistics")
    public String statistics(
            @PathVariable Long id,
            Model model) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();


        /*
         * ==========================================
         * 質問一覧
         * ==========================================
         */

        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );


        /*
         * ==========================================
         * 回答一覧
         * ==========================================
         */

        List<Response> responses =
                responseRepository.findByFormId(
                        id
                );


        /*
         * ==========================================
         * 質問ごとの選択肢
         * ==========================================
         */

        Map<Long, List<Choice>> questionChoices =
                new HashMap<>();


        for (Question question : questions) {

            questionChoices.put(
                    question.getId(),
                    choiceRepository.findByQuestionId(
                            question.getId()
                    )
            );
        }


        /*
         * ==========================================
         * 質問ごとの回答一覧
         * ==========================================
         */

        Map<Long, List<Answer>> questionAnswers =
                new HashMap<>();


        for (Question question : questions) {

            List<Answer> answers =
                    new ArrayList<>();


            for (Response response : responses) {

                List<Answer> responseAnswerList =
                        answerRepository.findByResponseId(
                                response.getId()
                        );


                for (Answer answer :
                        responseAnswerList) {

                    if (answer.getQuestion() != null
                            && answer.getQuestion()
                                    .getId()
                                    .equals(question.getId())) {

                        answers.add(
                                answer
                        );
                    }
                }
            }


            questionAnswers.put(
                    question.getId(),
                    answers
            );
        }


        /*
         * ==========================================
         * 選択肢ごとの回答数
         *
         * 質問ID
         *     ↓
         * 選択肢
         *     ↓
         * 回答数
         * ==========================================
         */

        Map<Long, Map<String, Integer>> choiceCounts =
                new HashMap<>();


        for (Question question : questions) {

            if (!question.getQuestionType()
                    .equals("radio")
                    && !question.getQuestionType()
                    .equals("checkbox")) {

                continue;
            }


            Map<String, Integer> counts =
                    new HashMap<>();


            /*
             * ==========================================
             * すべての選択肢を0票で登録
             * ==========================================
             */

            List<Choice> choices =
                    questionChoices.get(
                            question.getId()
                    );


            for (Choice choice : choices) {

                counts.put(
                        choice.getChoiceText(),
                        0
                );
            }


            /*
             * ==========================================
             * 回答を取得
             * ==========================================
             */

            List<Answer> answers =
                    questionAnswers.get(
                            question.getId()
                    );


            if (answers != null) {

                for (Answer answer : answers) {

                    String answerText =
                            answer.getAnswerText();


                    if (answerText == null
                            || answerText.isBlank()) {

                        continue;
                    }


                    /*
                     * ==========================================
                     * ラジオボタン
                     * ==========================================
                     */

                    if (question.getQuestionType()
                            .equals("radio")) {

                        counts.put(
                                answerText,
                                counts.getOrDefault(
                                        answerText,
                                        0
                                ) + 1
                        );
                    }


                    /*
                     * ==========================================
                     * チェックボックス
                     *
                     * 例：
                     *
                     * 赤,青
                     *
                     * ↓
                     *
                     * 赤 → 1
                     * 青 → 1
                     * ==========================================
                     */

                    else {

                        String[] selectedChoices =
                                answerText.split(",");


                        for (String selectedChoice :
                                selectedChoices) {

                            String choiceText =
                                    selectedChoice.trim();


                            if (choiceText.isEmpty()) {

                                continue;
                            }


                            counts.put(
                                    choiceText,
                                    counts.getOrDefault(
                                            choiceText,
                                            0
                                    ) + 1
                            );
                        }
                    }
                }
            }


            choiceCounts.put(
                    question.getId(),
                    counts
            );
        }


        /*
         * ==========================================
         * 質問ごとの回答率
         *
         * 回答数 ÷ 回答者数 × 100
         * ==========================================
         */

        Map<Long, Integer> answerRates =
                new HashMap<>();


        for (Question question : questions) {

            List<Answer> answers =
                    questionAnswers.get(
                            question.getId()
                    );


            int answerCount =
                    answers != null
                            ? answers.size()
                            : 0;


            int rate = 0;


            if (!responses.isEmpty()) {

                rate =
                        (int) Math.round(
                                (double) answerCount
                                / responses.size()
                                * 100
                        );
            }


            answerRates.put(
                    question.getId(),
                    rate
            );
        }


        /*
         * ==========================================
         * Modelへ渡す
         * ==========================================
         */

        model.addAttribute(
                "form",
                form
        );

        model.addAttribute(
                "questions",
                questions
        );

        model.addAttribute(
                "responses",
                responses
        );

        model.addAttribute(
                "questionChoices",
                questionChoices
        );

        model.addAttribute(
                "questionAnswers",
                questionAnswers
        );

        model.addAttribute(
                "choiceCounts",
                choiceCounts
        );

        model.addAttribute(
                "answerRates",
                answerRates
        );


        return "statistics";
    }


    /*
     * ==========================================
     * 分岐設定画面
     * ==========================================
     */

    @GetMapping("/form/{id}/branch")
    public String branchSettings(
            @PathVariable Long id,
            Model model) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();


        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );


        Map<Long, List<Choice>> questionChoices =
                new HashMap<>();


        for (Question question : questions) {

            questionChoices.put(
                    question.getId(),
                    choiceRepository.findByQuestionId(
                            question.getId()
                    )
            );
        }


        model.addAttribute(
                "form",
                form
        );

        model.addAttribute(
                "questions",
                questions
        );

        model.addAttribute(
                "questionChoices",
                questionChoices
        );


        return "branch-settings";
    }


    /*
     * ==========================================
     * 分岐設定保存
     * ==========================================
     */

    @PostMapping("/form/{id}/branch")
    public String saveBranchSettings(
            @PathVariable Long id,
            @RequestParam Map<String, String> params) {

        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );


        for (Question question : questions) {

            /*
             * ==========================================
             * ① 選択肢による分岐
             * ==========================================
             */

            List<Choice> choices =
                    choiceRepository.findByQuestionId(
                            question.getId()
                    );


            for (Choice choice : choices) {

                String key =
                        "choice-" + choice.getId();


                String nextQuestionId =
                        params.get(key);


                /*
                 * 未設定
                 *
                 * → 分岐なし
                 * → フォーム終了
                 */

                if (nextQuestionId == null
                        || nextQuestionId.isBlank()) {

                    choice.setNextQuestion(
                            null
                    );

                } else {

                    Long nextId =
                            Long.parseLong(
                                    nextQuestionId
                            );


                    Question nextQuestion =
                            questionRepository
                                    .findById(nextId)
                                    .orElseThrow();


                    choice.setNextQuestion(
                            nextQuestion
                    );
                }


                choiceRepository.save(
                        choice
                );
            }


            /*
             * ==========================================
             * ② 質問自身の次の質問
             * ==========================================
             */

            String questionKey =
                    "question-" + question.getId();


            String nextQuestionId =
                    params.get(questionKey);


            if (nextQuestionId == null
                    || nextQuestionId.isBlank()) {

                question.setNextQuestion(
                        null
                );

            } else {

                Long nextId =
                        Long.parseLong(
                                nextQuestionId
                        );


                Question nextQuestion =
                        questionRepository
                                .findById(nextId)
                                .orElseThrow();


                question.setNextQuestion(
                        nextQuestion
                );
            }


            questionRepository.save(
                    question
            );
        }


        return "redirect:/form/" + id + "/branch";
    }


    /*
     * ==========================================
     * フォーム削除
     * ==========================================
     */

    @PostMapping("/form/{id}/delete")
    public String deleteForm(
            @PathVariable Long id) {

        Form form =
                formRepository.findById(id)
                        .orElseThrow();


        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                id
                        );


        /*
         * ① Answerを削除
         */

        List<Response> responses =
                responseRepository.findByFormId(
                        id
                );


        for (Response response : responses) {

            List<Answer> answers =
                    answerRepository.findByResponseId(
                            response.getId()
                    );


            answerRepository.deleteAll(
                    answers
            );
        }


        /*
         * ② Responseを削除
         */

        responseRepository.deleteAll(
                responses
        );


        /*
         * ③ Choiceの分岐設定を解除
         */

        for (Question question : questions) {

            List<Choice> choices =
                    choiceRepository.findByQuestionId(
                            question.getId()
                    );


            for (Choice choice : choices) {

                choice.setNextQuestion(
                        null
                );

                choiceRepository.save(
                        choice
                );
            }
        }


        /*
         * ④ Questionの分岐設定を解除
         */

        for (Question question : questions) {

            question.setNextQuestion(
                    null
            );

            questionRepository.save(
                    question
            );
        }


        /*
         * ⑤ Choiceを削除
         */

        for (Question question : questions) {

            List<Choice> choices =
                    choiceRepository.findByQuestionId(
                            question.getId()
                    );


            choiceRepository.deleteAll(
                    choices
            );
        }


        /*
         * ⑥ Questionを削除
         */

        questionRepository.deleteAll(
                questions
        );


        /*
         * ⑦ Formを削除
         */

        formRepository.delete(
                form
        );


        return "redirect:/";
    }


    /*
     * ==========================================
     * 質問削除
     * ==========================================
     */

    @PostMapping("/form/{formId}/question/{questionId}/delete")
    public String deleteQuestion(
            @PathVariable Long formId,
            @PathVariable Long questionId) {

        Form form =
                formRepository.findById(formId)
                        .orElseThrow();


        Question question =
                questionRepository.findById(questionId)
                        .orElseThrow();


        /*
         * 別のフォームの質問を
         * 削除できないようにする
         */

        if (!question.getForm().getId()
                .equals(form.getId())) {

            throw new IllegalArgumentException(
                    "この質問は指定されたフォームに属していません。"
            );
        }


        /*
         * ① この質問への回答を削除
         */

        List<Response> responses =
                responseRepository.findByFormId(
                        formId
                );


        for (Response response : responses) {

            List<Answer> answers =
                    answerRepository.findByResponseId(
                            response.getId()
                    );


            for (Answer answer : answers) {

                if (answer.getQuestion() != null
                        && answer.getQuestion()
                                .getId()
                                .equals(questionId)) {

                    answerRepository.delete(
                            answer
                    );
                }
            }
        }


        /*
         * ② 他の質問からの分岐を解除
         */

        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                formId
                        );


        for (Question otherQuestion : questions) {

            /*
             * 質問自身の分岐先
             */

            if (otherQuestion.getNextQuestion() != null
                    && otherQuestion
                            .getNextQuestion()
                            .getId()
                            .equals(questionId)) {

                otherQuestion.setNextQuestion(
                        null
                );

                questionRepository.save(
                        otherQuestion
                );
            }


            /*
             * 選択肢の分岐先
             */

            List<Choice> choices =
                    choiceRepository.findByQuestionId(
                            otherQuestion.getId()
                    );


            for (Choice choice : choices) {

                if (choice.getNextQuestion() != null
                        && choice
                                .getNextQuestion()
                                .getId()
                                .equals(questionId)) {

                    choice.setNextQuestion(
                            null
                    );

                    choiceRepository.save(
                            choice
                    );
                }
            }
        }


        /*
         * ③ この質問の選択肢を削除
         */

        List<Choice> choices =
                choiceRepository.findByQuestionId(
                        questionId
                );


        for (Choice choice : choices) {

            choice.setNextQuestion(
                    null
            );

            choiceRepository.save(
                    choice
            );
        }


        choiceRepository.deleteAll(
                choices
        );


        /*
         * ④ 質問を削除
         */

        questionRepository.delete(
                question
        );


        /*
         * ⑤ 質問番号を振り直す
         */

        List<Question> remainingQuestions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                formId
                        );


        int order = 1;


        for (Question remainingQuestion :
                remainingQuestions) {

            remainingQuestion.setQuestionOrder(
                    order
            );

            questionRepository.save(
                    remainingQuestion
            );

            order++;
        }


        return "redirect:/form/" + formId;
    }


    /*
     * ==========================================
     * 質問を上へ移動
     * ==========================================
     */

    @GetMapping("/form/{formId}/question/{questionId}/up")
    public String moveQuestionUp(
            @PathVariable Long formId,
            @PathVariable Long questionId) {

        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                formId
                        );


        for (int i = 1;
             i < questions.size();
             i++) {

            Question current =
                    questions.get(i);


            Question previous =
                    questions.get(i - 1);


            if (current.getId().equals(questionId)) {

                int temp =
                        current.getQuestionOrder();


                current.setQuestionOrder(
                        previous.getQuestionOrder()
                );


                previous.setQuestionOrder(
                        temp
                );


                questionRepository.save(
                        current
                );


                questionRepository.save(
                        previous
                );


                break;
            }
        }


        return "redirect:/form/" + formId;
    }


    /*
     * ==========================================
     * 質問を下へ移動
     * ==========================================
     */

    @GetMapping("/form/{formId}/question/{questionId}/down")
    public String moveQuestionDown(
            @PathVariable Long formId,
            @PathVariable Long questionId) {

        List<Question> questions =
                questionRepository
                        .findByFormIdOrderByQuestionOrderAsc(
                                formId
                        );


        for (int i = 0;
             i < questions.size() - 1;
             i++) {

            Question current =
                    questions.get(i);


            Question next =
                    questions.get(i + 1);


            if (current.getId().equals(questionId)) {

                int temp =
                        current.getQuestionOrder();


                current.setQuestionOrder(
                        next.getQuestionOrder()
                );


                next.setQuestionOrder(
                        temp
                );


                questionRepository.save(
                        current
                );


                questionRepository.save(
                        next
                );


                break;
            }
        }


        return "redirect:/form/" + formId;
    }

}