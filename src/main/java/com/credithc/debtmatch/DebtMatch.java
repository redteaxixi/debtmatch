package com.credithc.debtmatch;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DebtMatch {

    public final int THRED_MAX = 100000;

    private List<FinanceT> financeT = new ArrayList<FinanceT>();
    private List<DebtT> debtT = new LinkedList<DebtT>();

    private List<FinanceT> financeTBackup = new ArrayList<FinanceT>();
    private List<DebtT> debtTBackup = new ArrayList<DebtT>();

    private List<MatchResult> result = new ArrayList();
    private int currentIndex;
    private int offset;

    public static void main(String[] args) {
        DebtMatch dm = new DebtMatch();
        try {
            dm.importExcel();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        dm.init();
        dm.match();
        dm.verify();
        try {
            dm.exportExcel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void match() {
        for (FinanceT oneF : financeT) {
            int moneyF = oneF.getMoney();
            if (moneyF > THRED_MAX) {
                // 分散投资：金额超过100000分的资金，需要匹配超过（包括）5笔不同的债权。
                int average, last;
                if (moneyF > THRED_MAX * 4) {
                    average = THRED_MAX;
                    last = moneyF - THRED_MAX * 4;
                } else {
                    average = moneyF / 5;
                    last = moneyF - average * 4;
                }
                offset = 0;
                for (int j = 0; j < 4; j++) {
                    FinanceT oneFF = new FinanceT(oneF.getId(), average, oneF.getType());
                    matchonce(oneFF, true);
                }
                FinanceT oneFF = new FinanceT(oneF.getId(), last, oneF.getType());
                matchonce(oneFF, true);
                currentIndex = currentIndex - offset;
            } else {
                matchonce(oneF, false);
            }
        }
        //System.out.print(result);
    }

    private void matchonce(FinanceT oneF, boolean noOffset) {
        if (currentIndex == debtT.size()) {
            return;
        }
        DebtT oneD = debtT.get(currentIndex);
        int moneyD = oneD.getMoney();
        int moneyF = oneF.getMoney();
        if (moneyD >= moneyF) {
            // entryF totally matched
            MatchResult oneMatch = new MatchResult(oneF.getId(), oneD.getId(), moneyF);
            result.add(oneMatch);
            if (moneyD == moneyF) {
                debtT.remove(oneD);
            } else {
                oneD.setMoney(moneyD - moneyF);
                if (noOffset) {
                    offset++;
                    currentIndex++;
                }
            }
        }
        if ((moneyF - moneyD > 0) && currentIndex < debtT.size()) {
            // entryF partially matched
            MatchResult oneMatch = new MatchResult(oneF.getId(), oneD.getId(), moneyD);
            result.add(oneMatch);
            oneF.setMoney(moneyF - moneyD);
            debtT.remove(oneD);
            matchonce(oneF, noOffset);
        }
    }

    private void init() {
        financeT.add(new FinanceT(1, 13, 1));
        financeT.add(new FinanceT(2, 500, 1));
        financeT.add(new FinanceT(3, 798, 1));
        financeT.add(new FinanceT(4, 200, 1));

        debtT.add(new DebtT("1", 567));
        debtT.add(new DebtT("2", 62));
        debtT.add(new DebtT("3", 1134));
        debtT.add(new DebtT("4", 467));
        debtT.add(new DebtT("5", 202));
    }

    private void importExcel() throws Exception {
        File file = new File("src/main/resources/testdata1.xlsx");
        XSSFWorkbook xssfWorkbook;
        try {
            xssfWorkbook = new XSSFWorkbook(file);
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
        int totalRows = xssfSheet.getLastRowNum();
        //读取Row,从第二行开始
        for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                //读取列，从第一列开始
                FinanceT oneF = new FinanceT();
                XSSFCell cell = xssfRow.getCell(0);
                oneF.setId(Integer.parseInt(cell.getRawValue()));
                cell = xssfRow.getCell(1);
                oneF.setMoney(Integer.parseInt(cell.getRawValue()));
                cell = xssfRow.getCell(2);
                oneF.setType(Integer.parseInt(cell.getRawValue()));
                financeT.add(oneF);
            }
        }

        financeTBackup.addAll(financeT);

        xssfSheet = xssfWorkbook.getSheetAt(1);
        totalRows = xssfSheet.getLastRowNum();
        //读取Row,从第二行开始
        for (int rowNum = 1; rowNum <= totalRows; rowNum++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowNum);
            if (xssfRow != null) {
                //读取列，从第一列开始
                DebtT oneD = new DebtT();
                XSSFCell cell = xssfRow.getCell(0);
                oneD.setId(cell.getStringCellValue());
                cell = xssfRow.getCell(1);
                oneD.setMoney(Integer.parseInt(cell.getRawValue()));
                debtT.add(oneD);
            }
        }

        debtTBackup.addAll(debtT);
    }

    private void exportExcel() throws Exception {
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet("result");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("理财id");
        cell = row.createCell(1);
        cell.setCellValue("债权id");
        cell = row.createCell(2);
        cell.setCellValue("匹配金额");
        for (int i = 0; i < result.size(); i++) {
            MatchResult matchResult = result.get(i);
            //数据每增加一行，表格就再生成一行
            row = sheet.createRow(i + 1);
            cell = row.createCell(0);
            cell.setCellValue(matchResult.getIdF());
            cell = row.createCell(1);
            cell.setCellValue(matchResult.getIdD());
            cell = row.createCell(2);
            cell.setCellValue(matchResult.getMoney());
        }

        FileOutputStream output = new FileOutputStream("src/main/resources/testdata2.xlsx");
        xssfWorkbook.write(output);
        output.close();
    }

    private void verify() {
        Map fMap = new HashMap<Integer, Integer>();
        Map dMap = new HashMap<Integer, Integer>();

        for (MatchResult matchResult: result) {
            int money = matchResult.getMoney();
            if (fMap.get(matchResult.getIdF()) == null) {
                fMap.put(matchResult.getIdF(), money);
            } else {
                fMap.put(matchResult.getIdF(), (Integer)fMap.get(matchResult.getIdF()) + money);
            }
            if (dMap.get(matchResult.getIdD()) == null) {
                dMap.put(matchResult.getIdD(), money);
            } else {
                dMap.put(matchResult.getIdD(), (Integer)dMap.get(matchResult.getIdD()) + money);
            }
        }
        System.out.println("理财匹配结果：");
        int totallyMatched = 0;
        int partiallyMatched = 0;
        for (FinanceT oneF: financeTBackup) {
            int id = oneF.getId();
            int money = oneF.getMoney();
            int matchMoney = (Integer) fMap.get(id);
            if (money == matchMoney) {
                totallyMatched ++;
            } else {
                partiallyMatched ++;
            }
            System.out.println("Id：" + id + " money: " + money + " matchMoney: " + matchMoney);
        }

        System.out.println("理财表数据条数：" + financeTBackup.size() + " 全匹数：" + totallyMatched + " 半匹数：" + partiallyMatched);

        System.out.println("债权匹配结果：");
        totallyMatched = 0;
        partiallyMatched = 0;
        for (DebtT oneD: debtTBackup) {
            String id = oneD.getId();
            int money = oneD.getMoney();
            int matchMoney = (Integer) dMap.get(id);
            if (money == matchMoney) {
                totallyMatched ++;
            } else {
                partiallyMatched ++;
            }
            System.out.println("Id：" + id + " money: " + money + " matchMoney: " + matchMoney);
        }

        System.out.println("债权表数据条数：" + debtTBackup.size() + " 全匹数：" + totallyMatched + " 半匹数：" + partiallyMatched);

    }

}