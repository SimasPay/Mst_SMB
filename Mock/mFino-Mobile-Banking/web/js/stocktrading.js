/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

var defaultPortfolio = "mFino-Client-portfolio";

function PRINT(msg)
{
    alert(msg);
}

function logMeIn()
{
    scope = "http://finance.google.com/finance/feeds/";
    var token = google.accounts.user.login(scope);
}

function setupMyService()
{
    var myService = new google.gdata.finance.FinanceService('GoogleInc-financejsguide-1.0');
    logMeIn();
    return myService;
}

function logMeOut()
{
    google.accounts.user.logout();
}

function checkOrCreatePortfolioTicker()
{
    var ticker = document.getElementById('symbol').value;

    var financeService = setupMyService();

    // Our new transaction is represented by this transaction entry object
    var transactionEntry = new google.gdata.finance.TransactionEntry();
    var transactionData = new google.gdata.finance.TransactionData();
    transactionData.setType('Buy');
    transactionData.setShares('1');
    transactionData.setNotes('mFino Quote Fetcher transaction');
    // Other transaction data could be specified here: setPrice, setDate, etc.
    // If these are unspecified, the transaction is treated like a "watchlist" item:
    // no investment performance is calculated for this part of the portfolio.
    transactionEntry.setTransactionData(transactionData);

    // This is the ticker symbol for the transaction that we are going to insert.
    // Note that the ticker is not a property of the transaction entry object; it is
    // specified in the URL to which to entry will be posted.

    // This method will run once the transaction has been created
    var transactionInsertedCallback = function(result) {
        var transactionData = result.entry.getTransactionData();
        PRINT('You created a new transaction of type "' +
            transactionData.getType() + '" for ' +
            transactionData.getShares() + ' units');
    };

    // A new transaction must be inserted into a portfolio.  The portfolio is
    // specified by Portfolio ID in the post URL.  In this example, we will search
    // for a portfolio starting with "JS-Client" and construct the post URL based on
    // this portfolio's edit URL when this callback runs.
    var findPortfolioCallback = function(result) {
        var targetTitle = 'mFino';
        var portfolioFound = false;

        // An array with all of the user's portfolios
        var entries = result.feed.entry;
        for (var i = 0; i < entries.length; i++) {
            var portfolioEntry = entries[i];
            var portfolioTitle = portfolioEntry.getTitle().getText();
            if (portfolioTitle.substring(targetTitle.length, 0) == targetTitle) {
                portfolioFound = true;
                PRINT('Found ['+targetTitle+'] portfolio, adding transaction...');

                // Now that we have found a portfolio to add the transaction to, we can
                // create the Post URI and insert the new transaction.
                var transactionPostUri = portfolioEntry.getEditLink().getHref() +
                '/positions/' + ticker + '/transactions';
                financeService.insertEntry(transactionPostUri,
                    transactionEntry,
                    transactionInsertedCallback,
                    handleErrorCallback,
                    google.gdata.finance.TransactionEntry);
                break;
            }
        }
        if (!portfolioFound) {
            PRINT('No portfolio exists with title starting ' + targetTitle +
                '.  Try the create portfolio example first.');
        }
    };

    // FinanceService methods may be supplied with an alternate callback for errors
    var handleErrorCallback = function(error) {
        PRINT(error);
    };

    PRINT('Retrieving a list of the user portfolios...');
    var portfolioFeedUrl =
    'http://finance.google.com/finance/feeds/default/portfolios';
    financeService.getPortfolioFeed(portfolioFeedUrl,
        findPortfolioCallback,
        handleErrorCallback);

    logMeOut();
}