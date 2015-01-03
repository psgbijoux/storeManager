import com.storemanager.service.ReportService;
import com.storemanager.service.ServiceLocator;
import com.storemanager.service.ServiceName;

public class TestReceiptPrint {
    public static void main(String[] args) {
        ReportService service = ServiceLocator.getService(ServiceName.REPORT_SERVICE);
        //service.generateSaleReport();
    }

}
