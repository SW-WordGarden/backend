package com.wordgarden.wordgarden.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wordgarden.wordgarden.dto.*;
import com.wordgarden.wordgarden.entity.*;
import com.wordgarden.wordgarden.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WqService {
    private static final Logger log = LoggerFactory.getLogger(WqService.class);

    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private WqinfoRepository wqinfoRepository;
    @Autowired
    private WqresultRepository wqresultRepository;
    @Autowired
    private WqwrongRepository wqwrongRepository;
    @Autowired
    private UserRepository userRepository;

    public List<WqResponseDto> generateQuiz() {
        List<Wqinfo> quiz = new ArrayList<>();
        String quizTitle = "앱 퀴즈 #" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);

        generateQuestions(quiz, allWords, quizTitle);

        List<Wqinfo> savedQuiz = wqinfoRepository.saveAll(quiz);
        return savedQuiz.stream().map(this::createEnhancedDto).collect(Collectors.toList());
    }

    private void generateQuestions(List<Wqinfo> quiz, List<Word> allWords, String quizTitle) {
        generateQuestionsOfType(quiz, allWords, quizTitle, "write", 0, 2);
        generateQuestionsOfType(quiz, allWords, quizTitle, "four", 2, 4);
        generateQuestionsOfType(quiz, allWords, quizTitle, "ox", 6, 4);
    }

    private void generateQuestionsOfType(List<Wqinfo> quiz, List<Word> allWords, String quizTitle, String type, int startIndex, int count) {
        for (int i = 0; i < count; i++) {
            generateQuestion(quiz, allWords.get(startIndex + i), quizTitle, type, startIndex + i);
        }
    }

    private void generateQuestion(List<Wqinfo> quiz, Word word, String quizTitle, String questionType, int index) {
        Wqinfo question = new Wqinfo();
        question.setWqId(questionType + index);
        question.setWqTitle(quizTitle);
        question.setWord(word);

        switch (questionType) {
            case "four":
                generateMultipleChoiceQuestion(question, wordRepository.findAll());
                break;
            case "write":
                generateWriteInQuestion(question);
                break;
            case "ox":
                generateOXQuestion(question, wordRepository.findAll());
                break;
        }

        quiz.add(question);
    }

    private void generateMultipleChoiceQuestion(Wqinfo question, List<Word> allWords) {
        question.setWqQuestion("다음 중 뜻으로 적절한 것을 선택하세요.");
        question.setWqAnswer(question.getWord().getWordInfo());

        List<String> options = generateOptions(question, allWords);
        question.setWqQuestion(question.getWqQuestion() + "\n" + String.join("\n", options));
    }

    private List<String> generateOptions(Wqinfo question, List<Word> allWords) {
        List<String> options = new ArrayList<>();
        options.add(question.getWord().getWordInfo());

        while (options.size() < 4) {
            String randomOption = allWords.get(new Random().nextInt(allWords.size())).getWordInfo();
            if (!options.contains(randomOption)) {
                options.add(randomOption);
            }
        }

        Collections.shuffle(options);
        return options;
    }

    private void generateWriteInQuestion(Wqinfo question) {
        question.setWqQuestion("다음 단어의 뜻을 정확하게 작성하세요.\n" + question.getWord().getWord());
        question.setWqAnswer(question.getWord().getWordInfo());
    }

    private void generateOXQuestion(Wqinfo question, List<Word> allWords) {
        question.setWqQuestion("다음 중 뜻이 적절하다면 O아니라면 X를 선택하세요.");
        boolean isCorrect = new Random().nextBoolean();

        if (isCorrect) {
            question.setWqQuestion(question.getWqQuestion() + "\n" + question.getWord().getWord() + " - " + question.getWord().getWordInfo());
            question.setWqAnswer("O");
        } else {
            Word randomWord = getRandomWordExcept(allWords, question.getWord());
            question.setWqQuestion(question.getWqQuestion() + "\n" + question.getWord().getWord() + " - " + randomWord.getWordInfo());
            question.setWqAnswer("X");
        }
    }

    private Word getRandomWordExcept(List<Word> allWords, Word exceptWord) {
        Word randomWord;
        do {
            randomWord = allWords.get(new Random().nextInt(allWords.size()));
        } while (randomWord.getWordId().equals(exceptWord.getWordId()));
        return randomWord;
    }

    private WqResponseDto createEnhancedDto(Wqinfo wqinfo) {
        WqResponseDto dto = new WqResponseDto();
        dto.setWqId(wqinfo.getWqId());
        dto.setWqTitle(wqinfo.getWqTitle());
        dto.setWordId(wqinfo.getWord().getWordId());
        dto.setWord(wqinfo.getWord().getWord());

        String questionType = determineQuestionType(wqinfo.getWqId());
        dto.setQuestionType(questionType);

        switch (questionType) {
            case "four":
                dto.setWqQuestion("다음 중 뜻으로 적절한 것을 선택하세요.");
                dto.setOptions(parseOptions(wqinfo.getWqQuestion()));
                break;
            case "write":
                dto.setWqQuestion(wqinfo.getWqQuestion());
                break;
            case "ox":
                dto.setWqQuestion(wqinfo.getWqQuestion());
                dto.setOptions(Arrays.asList("O", "X"));
                break;
        }

        return dto;
    }

    private List<String> parseOptions(String question) {
        String[] parts = question.split("\n");
        return Arrays.asList(Arrays.copyOfRange(parts, 1, parts.length));
    }

    @Transactional
    public void saveResults(WqSubmissionDto submission) {
        log.info("Processing submission for user: {}", submission.getUid());

        User user = userRepository.findById(submission.getUid())
                .orElseThrow(() -> new RuntimeException("User not found: " + submission.getUid()));

        List<Wqresult> results = new ArrayList<>();
        List<Wqwrong> wrongs = new ArrayList<>();

        for (WqAnswerDto answer : submission.getAnswers()) {
            try {
                processAnswer(user, answer, results, wrongs);
            } catch (Exception e) {
                log.error("Error processing answer: {}", answer, e);
            }
        }

        wqresultRepository.saveAll(results);
        wqwrongRepository.saveAll(wrongs);
        log.info("Saved {} results and {} wrongs for user: {}", results.size(), wrongs.size(), user.getUid());
    }

    private void processAnswer(User user, WqAnswerDto answer, List<Wqresult> results, List<Wqwrong> wrongs) {
        Wqinfo wqinfo = wqinfoRepository.findById(answer.getWqId())
                .orElseThrow(() -> new RuntimeException("Question not found: " + answer.getWqId()));

        Wqresult result = createWqresult(user, wqinfo, answer.getUWqA());
        results.add(result);

        if (!result.getWqCheck()) {
            wrongs.add(createWqwrong(user, wqinfo));
        }
    }

    private Wqresult createWqresult(User user, Wqinfo wqinfo, String userAnswer) {
        Wqresult result = new Wqresult();
        result.setWqInfo(wqinfo);
        result.setUser(user);
        result.setUWqA(userAnswer);
        result.setWqCheck(compareAnswer(wqinfo, userAnswer));
        result.setTime(new Timestamp(System.currentTimeMillis()));
        return result;
    }

    private Wqwrong createWqwrong(User user, Wqinfo wqinfo) {
        Wqwrong wrong = new Wqwrong();
        wrong.setWqInfo(wqinfo);
        wrong.setWord(wqinfo.getWord());
        wrong.setUser(user);
        return wrong;
    }

    private boolean compareAnswer(Wqinfo wqinfo, String userAnswer) {
        if (userAnswer == null) {
            log.warn("User answer is null for wqId: {}", wqinfo.getWqId());
            return false;
        }

        String correctAnswer = wqinfo.getWqAnswer();
        String questionType = determineQuestionType(wqinfo.getWqId());

        return correctAnswer.equalsIgnoreCase(userAnswer.trim());
    }

    private String determineQuestionType(String wqId) {
        if (wqId.startsWith("four")) return "four";
        if (wqId.startsWith("write")) return "write";
        if (wqId.startsWith("ox")) return "ox";
        log.warn("Unknown question type for wqId: {}", wqId);
        return "unknown";
    }

    public List<WrongAnswerDto> getWrongAnswers(String userId) {
        List<Wqwrong> wrongAnswers = wqwrongRepository.findByUserUid(userId);
        return wrongAnswers.stream()
                .map(this::convertToWrongAnswerDto)
                .collect(Collectors.toList());
    }

    private WrongAnswerDto convertToWrongAnswerDto(Wqwrong wrong) {
        WrongAnswerDto dto = new WrongAnswerDto();
        dto.setWqId(wrong.getWqInfo().getWqId());
        dto.setWord(wrong.getWord().getWord());
        dto.setWordInfo(wrong.getWord().getWordInfo());
        return dto;
    }

    private final ObjectMapper objectMapper;

    @Autowired
    public WqService(WordRepository wordRepository, WqinfoRepository wqinfoRepository,
                     WqresultRepository wqresultRepository, WqwrongRepository wqwrongRepository,
                     UserRepository userRepository, ObjectMapper objectMapper) {
        this.wordRepository = wordRepository;
        this.wqinfoRepository = wqinfoRepository;
        this.wqresultRepository = wqresultRepository;
        this.wqwrongRepository = wqwrongRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }
}