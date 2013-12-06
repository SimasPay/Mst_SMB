package com.mfino.dao.query;

import com.mfino.domain.Subscriber;

public class CardInfoQuery extends BaseQuery {

    private Subscriber _subscriber;
    private Boolean _showBothConfirmAndActiveCards;
    private Boolean _showBothRegisteredAndActiveCards;
    private Integer _cardStatus;
    private Boolean isConfirmationRequired;

    public Integer getCardStatus() {
        return _cardStatus;
    }

    public void setCardStatus(Integer _cardStatus) {
        this._cardStatus = _cardStatus;
    }

    public Boolean ShowBothConfirmAndActiveCards() {
        return _showBothConfirmAndActiveCards;
    }

    public void setShowBothConfirmAndActiveCards(Boolean _showBothConfirmAndActiveCards) {
        this._showBothConfirmAndActiveCards = _showBothConfirmAndActiveCards;
    }

   
    public Subscriber getSubscriber() {
        return _subscriber;
    }

    public void setSubscriber(Subscriber _subscriber) {
        this._subscriber = _subscriber;
    }

	public void setIsConfirmationRequired(Boolean isConfirmationRequired) {
		this.isConfirmationRequired = isConfirmationRequired;
}

	public Boolean getIsConfirmationRequired() {
		return isConfirmationRequired;
	}

	public void setShowBothRegisteredAndActiveCards(
			Boolean _showBothRegisteredAndActiveCards) {
		this._showBothRegisteredAndActiveCards = _showBothRegisteredAndActiveCards;
	}

	public Boolean showBothRegisteredAndActiveCards() {
		return _showBothRegisteredAndActiveCards;
	}
}
