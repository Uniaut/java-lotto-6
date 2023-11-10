package lotto.presentation;

import camp.nextstep.edu.missionutils.Console;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import lotto.application.LottoService;
import lotto.domain.LottoResult;
import lotto.domain.LottoReward;
import lotto.domain.RewardConfiguration;
import lotto.domain.Ticket;


public class LottoController {
    private final LottoService lottoService;

    public LottoController() {
        lottoService = new LottoService();
    }

    private void printOneTicket(Ticket ticket) {
        List<String> numberTexts = ticket.getNumbers().stream()
                .map(String::valueOf)
                .toList();
        String ticketText = String.join(", ", numberTexts);
        System.out.println("[" + ticketText + "]");
    }

    private void printTickets(List<Ticket> tickets) {
        System.out.println(tickets.size() + "개를 구매했습니다.");
        tickets.forEach(this::printOneTicket);
        System.out.println();
    }

    private void askMoney() {
        System.out.println("구입금액을 입력해 주세요.");
        String moneyText = Console.readLine();
        try {
            List<Ticket> tickets = lottoService.buyLotto(moneyText);
            printTickets(tickets);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            askMoney();
        }
    }

    private void askDrawResults() {
        System.out.println("당첨 번호를 입력해 주세요.");
        String numbersText = Console.readLine();
        System.out.println();
        System.out.println("보너스 번호를 입력해 주세요.");
        String bonusText = Console.readLine();
        System.out.println();
        try {
            lottoService.setDrawResult(numbersText, bonusText);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            askDrawResults();
        }
    }

    private String formatInteger(int number) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");
        return decimalFormat.format(number);
    }

    private String formatDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###.##%");
        return decimalFormat.format(number);
    }

    private void printOneStatisticEntry(LottoResult lottoResult, Long count) {
        String matchText = "%d개 일치";
        if (lottoResult.isBonusMatch()) {
            matchText += ", 보너스 볼 일치";
        }
        matchText = matchText.formatted(lottoResult.matchNumbers());

        String oneEntryFormat = "%s (%s원) - %s개";
        System.out.println(oneEntryFormat.formatted(
                matchText,
                formatInteger(RewardConfiguration.getReward(lottoResult)),
                formatInteger(count.intValue())
        ));
    }

    private void printStatistic(Map<LottoResult, Long> lottoStatistic) {
        for (var entry : RewardConfiguration.values()) {
            printOneStatisticEntry(entry.lottoResult, lottoStatistic.getOrDefault(entry.lottoResult, 0L));
        }
    }

    private void printReward(LottoReward lottoReward) {
        int rewardSum = lottoReward.claim();
        int investment = lottoService.getInvestment();
        System.out.printf("총 수익률은 %s입니다.%n", formatDouble((double) rewardSum / investment));
    }

    private void printResult() {
        lottoService.checkLottoStatistic();

        System.out.println("당첨 통계");
        System.out.println("---");

        printStatistic(lottoService.getLottoStatistic());

        printReward(lottoService.getLottoReward());
    }

    public void run() {
        askMoney();

        askDrawResults();

        printResult();
    }
}
