package com.twb.pokerapp.utils.sql.validator.impl;

import com.twb.pokerapp.utils.http.message.PlayersServerMessages;
import com.twb.pokerapp.utils.sql.SqlClient;
import com.twb.pokerapp.utils.sql.validator.DbValidator;


public class TexasHoldemDbValidator extends DbValidator {

    public TexasHoldemDbValidator(SqlClient sqlClient) {
        super(sqlClient);
    }

    @Override
    protected void onValidateEndOfRun(PlayersServerMessages messages) {

    }
}
