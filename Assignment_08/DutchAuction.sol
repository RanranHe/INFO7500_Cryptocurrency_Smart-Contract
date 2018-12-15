pragma solidity ^0.4.9;

contract DutchAuction {
    address ownerAddress;
    uint256 reservePrice;
    address judgeAddress;
    uint256 numBlocksActionOpen;
    uint256 offerPriceDecrement;
    uint startBlockNumber;
    address winnerAddress;
    uint winningBid;
    bool endAuction;
    bool finalized;

    function DutchAuction(uint256 _reservePrice, address _judgeAddress, uint256 _numBlocksAuctionOpen, uint256 _offerPriceDecrement) public {
        reservePrice = _reservePrice;
        judgeAddress = _judgeAddress;
        numBlocksActionOpen = _numBlocksAuctionOpen;
        offerPriceDecrement = _offerPriceDecrement;
        ownerAddress = msg.sender;
        startBlockNumber = block.number;
    }

    function bid() public payable returns(address) {
        require(!endAuction);
        require(block.number < (startBlockNumber + numBlocksActionOpen));
        require(msg.value >= (reservePrice + (offerPriceDecrement * (startBlockNumber + numBlocksActionOpen - block.number))));

        endAuction = true;

        if(judgeAddress == 0){
            ownerAddress.transfer(msg.value);
        }else{
            winnerAddress = msg.sender;
            winningBid = msg.value;
        }
    }

    function finalize() public {
        require(endAuction && !finalized);
        require(msg.sender == judgeAddress || msg.sender == winnerAddress);
        finalized = true;
        ownerAddress.transfer(winningBid);
    }

    function refund(uint256 refundAmount) public {
        require(endAuction && !finalized);
        require(msg.sender == judgeAddress);
        finalized = true;
        winnerAddress.transfer(refundAmount);
    }

    //for testing framework
    function nop() public returns(bool) {
        return true;
    }
}