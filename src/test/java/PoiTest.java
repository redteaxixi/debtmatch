/**
 * Created by Administrator on 2018/1/5.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

class User {
    String firstName;
    String lastName;
    int age;

    public User(String firstName, String lastName, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
public class PoiTest {
    //将生成好的Excel文件，放到硬盘上
    public void writeToDisk() {
        XSSFWorkbook wb = new XSSFWorkbook();
        //生成一个sheet1
        XSSFSheet sheet = wb.createSheet("sheet1");
        //为sheet1生成第一行，用于放表头信息
        XSSFRow row = sheet.createRow(0);
        //第一行的第一个单元格的值为  ‘序号’
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("序号");
        cell = row.createCell(1);
        cell.setCellValue("姓");
        cell = row.createCell(2);
        cell.setCellValue("名");
        cell = row.createCell(3);
        cell.setCellValue("年龄");
        User u = new User("lanxi", "liu", 5);
        List<User> list = new ArrayList<User>();
        list.add(u);
        //获得List中的数据，并将数据放到Excel中
        for (int i = 0; i < list.size(); i++) {
            User user = list.get(i);
            //数据每增加一行，表格就再生成一行
            row = sheet.createRow(i + 1);
            //第一个单元格，放序号随着i的增加而增加
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            //第二个单元格放firstname
            cell = row.createCell(1);
            cell.setCellValue(user.getFirstName());
            //第三个单元格放lastname
            cell = row.createCell(2);
            cell.setCellValue(user.getLastName());
            //第四个单元格放age
            cell = row.createCell(3);
            cell.setCellValue(user.getAge());
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            wb.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] content = os.toByteArray();
        File file = new File("src/test/resources/testdata2.xlsx");//Excel文件生成后存储的位置。
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content);
            os.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        PoiTest pt = new PoiTest();
        pt.writeToDisk();

    }
}