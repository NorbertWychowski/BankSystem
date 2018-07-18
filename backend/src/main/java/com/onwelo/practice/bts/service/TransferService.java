package com.onwelo.practice.bts.service;

import com.onwelo.practice.bts.entity.Transfer;
import com.onwelo.practice.bts.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransferService {

    @Autowired
    private TransferRepository transferRepository;

    public List<Transfer> getAllTransfers() {
        return new ArrayList<>(transferRepository.findAll());
    }

    public Transfer getTransferById(Long id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("could not fund transfer with id: " + id));
    }

    public void addTransfer(Transfer transfer) {
        transferRepository.save(transfer);
    }

    public void updateTransfer(Transfer transfer) {
        transferRepository.save(transfer);
    }

    public void deleteTransfer(Long id) {
        transferRepository.deleteById(id);
    }
}