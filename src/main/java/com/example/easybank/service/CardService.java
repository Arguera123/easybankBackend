package com.example.easybank.service;

import com.example.easybank.domain.entity.Card;

public interface CardService {
    public Card create() throws Exception;
    public Card findMyOwnCard() throws Exception;
}
