package com.onwelo.practice.bts.session;

import com.onwelo.practice.bts.entity.BankAccount;
import com.onwelo.practice.bts.entity.Transfer;
import com.onwelo.practice.bts.ftp.FtpService;
import com.onwelo.practice.bts.service.CsvService;
import com.onwelo.practice.bts.service.TransferService;
import com.onwelo.practice.bts.utils.TransferStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

@Component
public class SessionOutgoing {
    private static org.slf4j.Logger Logger = LoggerFactory.getLogger(CsvService.class);
    private ArrayList<Transfer> transfers;

    @Autowired
    private TransferService transferService;

    @Autowired
    private CsvService csvService;

    @Autowired
    private FtpService ftpService;

    // scheduled method
    void someMainMethod() {
        transfers = getTransfers();
        if (!Objects.requireNonNull(transfers).isEmpty()) {
            if (sendTransfers(getFileInputStream())) {
                updateBankAccounts();
                updateTransfers();
            }
        }
    }

    private ArrayList<Transfer> getTransfers() {
        ArrayList<Transfer> transfers = (ArrayList<Transfer>) transferService.getTransfersByStatus(TransferStatus.APPROVED);
        if (!transfers.isEmpty()) {
            return transfers;
        } else {
            return null;
        }
    }

    private InputStream getFileInputStream() {
        if (transfers != null) {
            try {
                return new FileInputStream(csvService.getCsvFromTransfers(transfers, "outgoingTransfers.csv"));
            } catch (FileNotFoundException e) {
                Logger.debug(e.getMessage(), e);
            }
        } else {
            return null;
        }

        return null;
    }

    private Boolean sendTransfers(InputStream inputStream) {
        if (ftpService.addFile(inputStream, "testOutgoingCsvFile.csv")) {
            Logger.info("Successful upload outgoing transfers csv to ftp");
            return true;
        } else {
            Logger.debug("Failed upload outgoing transfers csv to ftp");
            return false;
        }
    }

    private void updateBankAccounts() {
        for (Transfer transfer : transfers) {
            BankAccount bankAccount = transfer.getAccountId();
            bankAccount.setMoneyAmount(bankAccount.getMoneyAmount().subtract(transfer.getValue()));
        }
    }

    private void updateTransfers() {
        transfers.forEach(transfer -> transfer.setStatus(TransferStatus.REALIZED));
    }
}
