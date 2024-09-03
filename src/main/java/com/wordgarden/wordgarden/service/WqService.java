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
    @Autowired
    private GardenService gardenService;

    @Transactional
    public List<WqResponseDto> generateAndSaveNewQuiz() {
        List<Wqinfo> newQuiz = new ArrayList<>();
        String quizTitle = "앱 퀴즈_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));

        List<Word> allWords = wordRepository.findAll();
        Collections.shuffle(allWords);

        generateQuestions(newQuiz, allWords, quizTitle);

        List<Wqinfo> savedQuiz = wqinfoRepository.saveAll(newQuiz);
        return savedQuiz.stream().map(this::createEnhancedDto).collect(Collectors.toList());
    }

    private void generateQuestions(List<Wqinfo> quiz, List<Word> allWords, String quizTitle) {
        int startIndex = (int) wqinfoRepository.count();
        generateQuestionsOfType(quiz, allWords, quizTitle, "write", startIndex, 2);
        generateQuestionsOfType(quiz, allWords, quizTitle, "four", startIndex + 2, 4);
        generateQuestionsOfType(quiz, allWords, quizTitle, "ox", startIndex + 6, 4);
    }

    private void generateQuestionsOfType(List<Wqinfo> quiz, List<Word> allWords, String quizTitle, String type, int startIndex, int count) {
        for (int i = 0; i < count; i++) {
            generateQuestion(quiz, allWords.get(i % allWords.size()), quizTitle, type, startIndex + i);
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
        question.setWqQuestion("다음 뜻에 해당하는 단어를 작성하세요.\n" + question.getWord().getWordInfo());
        question.setWqAnswer(question.getWord().getWord());
    }

    private void generateOXQuestion(Wqinfo question, List<Word> allWords) {
        question.setWqQuestion("다음 단어와 뜻이 올바르게 연결되었다면 O, 아니라면 X를 선택하세요.");
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
        dto.setCorrectAnswer(wqinfo.getWqAnswer());  // Set the correct answer here as well

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
        User user = userRepository.findById(submission.getUid())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + submission.getUid()));

        user.setUParticipate(user.getUParticipate()+1);
        List<Wqresult> results = new ArrayList<>();
        List<Wqwrong> wrongs = new ArrayList<>();
        int correctAnswers = 0;

        for (WqAnswerDto answer : submission.getAnswers()) {
            try {
                boolean isCorrect = processAnswer(user, answer, results, wrongs);
                if (isCorrect) {
                    correctAnswers++;
                }
            } catch (Exception e) {
                log.error("답변 처리 중 오류 발생: {}", answer, e);
            }
        }

        // 포인트와 코인 증가
        int pointsEarned = correctAnswers * 25;
        int currentPoints = user.getUPoint() != null ? user.getUPoint() : 0;
        user.setUPoint(currentPoints + pointsEarned);
        userRepository.save(user);

        try {
            gardenService.increaseCoins(user.getUid(), pointsEarned);
        } catch (Exception e) {
            log.error("코인 증가 중 오류 발생: {}", e.getMessage());
        }

        wqresultRepository.saveAll(results);
        wqwrongRepository.saveAll(wrongs);
        log.info("사용자 {}의 {} 결과와 {} 오답을 저장했습니다. 획득 포인트: {}", user.getUid(), results.size(), wrongs.size(), pointsEarned);
    }

    private boolean processAnswer(User user, WqAnswerDto answer, List<Wqresult> results, List<Wqwrong> wrongs) {
        Wqinfo wqinfo = wqinfoRepository.findById(answer.getWqId())
                .orElseThrow(() -> new RuntimeException("문제를 찾을 수 없습니다: " + answer.getWqId()));

        Wqresult result = createWqresult(user, wqinfo, answer.getUWqA());
        results.add(result);

        if (!result.getWqCheck()) {
            addUniqueWqwrong(user, wqinfo, wrongs);
        }

        return result.getWqCheck();
    }

    private void addUniqueWqwrong(User user, Wqinfo wqinfo, List<Wqwrong> wrongs) {
        String wordId = wqinfo.getWord().getWordId();
        boolean exists = wqwrongRepository.existsByUserAndWordWordId(user, wordId);

        if (!exists) {
            Wqwrong wrong = createWqwrong(user, wqinfo);
            wrongs.add(wrong);
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

    public Set<String> getQuizTitlesByUserId(String userId) {
        return wqresultRepository.findDistinctWqTitlesByUserId(userId);
    }

    // 타이틀로 퀴즈 가져오기
//    public List<WqResponseDto> getQuizByTitleWithUserAnswers(String wqTitle, String userId) {
//        List<Wqinfo> quizQuestions = wqinfoRepository.findByWqTitle(wqTitle);
//        List<WqResponseDto> responseDtos = new ArrayList<>();
//
//        for (Wqinfo question : quizQuestions) {
//            WqResponseDto dto = createEnhancedDto(question);
//
//            // 사용자 작성 답안
//            Optional<Wqresult> result = wqresultRepository.findByWqInfoAndUserUid(question, userId);
//            result.ifPresent(wqresult -> dto.setUserAnswer(wqresult.getUWqA()));
//
//            // 정답
//            dto.setCorrectAnswer(question.getWqAnswer());
//
//            responseDtos.add(dto);
//        }
//
//        return responseDtos;
//    }

    public List<WqResponseDto> getQuizByTitleWithUserAnswers(String wqTitle, String userId) {
        List<Wqinfo> quizQuestions = wqinfoRepository.findByWqTitle(wqTitle);
        List<WqResponseDto> responseDtos = new ArrayList<>();

        for (Wqinfo question : quizQuestions) {
            WqResponseDto dto = createEnhancedDto(question);

            // 사용자 작성 답안
            Optional<Wqresult> result = wqresultRepository.findByWqInfoAndUserUid(question, userId);
            result.ifPresent(wqresult -> dto.setUserAnswer(wqresult.getUWqA()));

            // 정답
            dto.setCorrectAnswer(question.getWqAnswer());

            responseDtos.add(dto);
        }

        return responseDtos;
    }

    public List<WqResponseDto> getQuizByTitle(String wqTitle) {
        List<Wqinfo> quizQuestions = wqinfoRepository.findByWqTitle(wqTitle);
        return quizQuestions.stream()
                .map(question -> {
                    WqResponseDto dto = createEnhancedDto(question);
                    dto.setCorrectAnswer(question.getWqAnswer());  // 정답 설정
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 사용자 점수 반환
    public Map<String, Long> getUserQuizStats(String userId) {
        log.info("Calculating quiz stats for user: {}", userId);

        long totalQuestions = wqresultRepository.countByUserUid(userId);
        long correctAnswers = wqresultRepository.countByUserUidAndWqCheckTrue(userId);

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalQuestions", totalQuestions);
        stats.put("correctAnswers", correctAnswers);

        log.info("User {} stats: total questions - {}, correct answers - {}",
                userId, totalQuestions, correctAnswers);

        return stats;
    }

}