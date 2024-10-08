import { Checkbox, Radio, Space } from "antd";
import { useAppSelector } from "../../redux/hooks";
type Props = {
  examId: string;
  showAnswer?: boolean;
  userAnswer?: number[]
  quizResponse: QuizResponse;
  answerResponseList: AnswerResponse[];
  handleAnswerClick?: (quizResponse: QuizResponse, answers: number[]) => void;
};
const AnswerListElement = ({
  examId,
  userAnswer,
  showAnswer,
  quizResponse,
  answerResponseList,
  handleAnswerClick,
}: Props) => {
  const selectedAnswer = useAppSelector(
    (state) => state.userExams?.[examId]?.[quizResponse.id]
  );
  const value = userAnswer ?? selectedAnswer;
  return (
    <>
      {quizResponse.quiz_type == "SINGLE_CHOICE" && (
        <Radio.Group
          value={value?.[0] ?? -1}
          disabled={showAnswer}
          onChange={(e) =>
            handleAnswerClick
              ? handleAnswerClick(quizResponse, [e.target.value])
              : {}
          }
        >
          <Space direction="vertical">
            {answerResponseList.map((answer) => (
              <Radio
                key={`answer-radio-${answer.id}`}
                className="d-block"
                value={answer.id}
              >
                <span className="fs-5">{answer.answer}</span>
                {showAnswer && (
                  <>
                    {answer.correct && (
                      <i className="text-success bi bi-check-circle-fill"></i>
                    )}
                  </>
                )}
              </Radio>
            ))}
          </Space>
        </Radio.Group>
      )}
      {quizResponse.quiz_type == "MULTIPLE_CHOICE" && (
        <Checkbox.Group
          value={value}
          disabled={showAnswer}
          onChange={(e) => handleAnswerClick ? handleAnswerClick(quizResponse, e) : {}}
        >
          <Space direction="vertical">
            {answerResponseList.map((answer) => (
              <Checkbox key={`answer-checkbox-${answer.id}`} value={answer.id}>
                <span className="fs-5">{answer.answer}</span>
                {showAnswer && (
                  <>
                    {answer.correct && (
                      <i className="text-success bi bi-check-circle-fill"></i>
                    )}
                  </>
                )}
              </Checkbox>
            ))}
          </Space>
        </Checkbox.Group>
      )}
    </>
  );
};

export default AnswerListElement;
