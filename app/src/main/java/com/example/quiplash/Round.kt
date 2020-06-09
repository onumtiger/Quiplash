package com.example.quiplash


class Round {
    var voters: List<String> = listOf()
    var opponents: List<RoundUser> = listOf()
    //var winner: RoundUser?= null
    //var loser: RoundUser?= null

    constructor(votersArr: List<String>, opponentsArr: List<RoundUser>) {
        voters = votersArr
        opponents = opponentsArr
    }

    /*constructor() {
            this.voters = listOf("1ZqX1o543dZzMW4fCJL3pVvloZ83", "4FSb0bD9w9SkKoBm5OTu47dxi2v2")
            this.opponents = listOf("4VJCSPx5F8AhZ5GkdeEa", "70GSUacYuHT03uoos8iSBPhERGl1")
            this.winner = RoundUser("4VJCSPx5F8AhZ5GkdeEa")
            this.loser = RoundUser("70GSUacYuHT03uoos8iSBPhERGl1")
    }*/

    constructor()


    /*fun setResult(userW: RoundUser, userL: RoundUser){
        winner = userW
        loser = userL
    }*/

}