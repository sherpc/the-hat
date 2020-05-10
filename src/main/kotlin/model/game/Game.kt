package model.game

import java.util.*

data class GameSettings(val title: String, val wordsCount: Int, val playersCount: Int, val explainTimeoutSeconds: Int)

enum class PlayerState {
    SelectingWords, ReadyToPlay
}

enum class GameState {
    GatheringParty, Playing, Finished
}

enum class Round {
    DescribeInWords, Show, DescribeInOneWord
}

data class Player(
    val id: String,
    val name: String,
    val words: Set<String>,
    val state: PlayerState,
    val score: Int) {

    fun guess(): Player {
        return copy(score = score + 1)
    }
}

data class Team(val firstPlayerId: String, val secondPlayerId: String) {
    fun toPair(): ExplainPair {
        return ExplainPair(firstPlayerId, secondPlayerId)
    }
}

data class ExplainPair(val explainerId: String, val listenerId: String) {
    fun reverse(): ExplainPair {
        return copy(explainerId = listenerId, listenerId = explainerId)
    }
}

typealias ScoresByPlayerId = MutableMap<String, Int>
typealias ScoresByRound = Map<Round, ScoresByPlayerId>

data class LastExplainerScore(
    val previousScore: Int = 0,
    val currentScore: Int = 0) {

    fun incScore(): LastExplainerScore {
        return copy(currentScore = currentScore + 1)
    }

    fun nextTeam(): LastExplainerScore {
        return copy(currentScore = 0, previousScore = currentScore)
    }
}

data class Game private constructor(
    val id: String,
    val settings: GameSettings,
    val state: GameState = GameState.GatheringParty,
    val players: Map<String, Player> = emptyMap(),
    val deck: Set<String> = emptySet(),
    val initialDeckSize: Int = 0,
    val teams: List<Team> = emptyList(),
    val pairs: List<ExplainPair> = emptyList(),
    val currentTeam: Int = 0,
    val round: Round = Round.DescribeInWords,
    val scores: ScoresByRound = Round.values().map { Pair(it, mutableMapOf<String, Int>()) }.toMap(),
    val lastExplainerScore: LastExplainerScore = LastExplainerScore()) {

    companion object {
        fun build(id: String, settings: GameSettings): Game {
            return Game(id, settings)
        }
    }

    fun setPlayerWords(playerId: String, newWords: Set<String>): Game {
        if (settings.wordsCount != newWords.size)
            throw IllegalArgumentException("Wrong number of words (${newWords.size} instead of ${settings.wordsCount} expected).")
        return updatePlayer(playerId) {
            it.copy(words = newWords.toSet(), state = PlayerState.ReadyToPlay)
        }.startIfReady()
    }

    fun wordExplained(explainerPlayerId: String, word: String): Game {
        val currentPlayer = players[explainerPlayerId]

        if (currentPlayer == null || pairs[currentTeam].explainerId != currentPlayer.id)
            throw java.lang.IllegalArgumentException("Current player not explainer!")

        val gameWithUpdatedStats = updatePlayer(explainerPlayerId) { it.guess() }.incrementScores(explainerPlayerId)

        val newDeck = deck.minusElement(word)

        if (newDeck.isEmpty())
            return gameWithUpdatedStats.nextRound()

        return gameWithUpdatedStats.copy(deck = newDeck)
    }

    fun setDeck(playerId: String, newDeck: Set<String>): Game {
        val currentPlayer = players[playerId]

        if (currentPlayer == null || pairs[currentTeam].explainerId != currentPlayer.id)
            throw java.lang.IllegalArgumentException("Current player not explainer!")

        val gameWithUpdatedStats = updatePlayer(playerId) { it.guess() }.incrementScores(playerId)

        if (newDeck.isEmpty())
            return gameWithUpdatedStats.nextRound()

        return gameWithUpdatedStats.copy(deck = newDeck)
    }

    fun nextTeam(playerId: String): Game {
        val currentPlayer = players[playerId]
        if (currentPlayer == null || pairs[currentTeam].explainerId != currentPlayer.id)
            throw java.lang.IllegalArgumentException("Current player not explainer!")

        return nextTeamInternal()
    }

    private fun nextTeamInternal(): Game = copy(currentTeam = (currentTeam + 1) % pairs.size, lastExplainerScore = lastExplainerScore.nextTeam())

    fun joinGame(player: Player): Game {
        // Thread.sleep(TimeUnit.MILLISECONDS.toMillis(500))
        if (state != GameState.GatheringParty)
            throw IllegalArgumentException("Game already started.")
        if (players.size >= settings.playersCount)
            throw IllegalArgumentException("Game already full of players.")
        val players = players + Pair(player.id, player)
        return copy(players = players)
    }

    private fun incrementScores(explainerPlayerId: String): Game {
        scores[round]!![explainerPlayerId] = (scores[round]?.get(explainerPlayerId) ?: 0) + 1
        return this.copy(lastExplainerScore = lastExplainerScore.incScore())
    }

    private fun startIfReady(): Game {
        return if (readyToStart()) startGame() else this
    }

    private fun updatePlayer(playerId: String, playerUpdate: (player: Player) -> Player): Game {
        val currentPlayer = players[playerId]
        if (currentPlayer != null) {
            val newPlayer = playerUpdate(currentPlayer)
            val players = players + Pair(playerId, newPlayer)
            return copy(players = players)
        }
        return this
    }

    private fun readyToStart(): Boolean {
        return settings.playersCount == players.values.size
                && players.values.all { it.state == PlayerState.ReadyToPlay }
    }

    private fun startGame(): Game {
        val players = players.values
        val teams = players.shuffled().chunked(2) { twoPlayers -> Team(twoPlayers[0].id, twoPlayers[1].id) }
        val pairsFromTeams = teams.map(Team::toPair)
        val reversedPairs = pairsFromTeams.map(ExplainPair::reverse)
        val pairs = pairsFromTeams + reversedPairs
        return copy(state = GameState.Playing, pairs = pairs, teams = teams, currentTeam = 0)
            .resetDeck()
            .resetInitialDeckSize()
    }

    private fun resetDeck(): Game {
        val players = players.values
        val deck = players.fold(emptyList<String>()) { acc, player ->  acc + player.words}.toSet()
        return copy(deck = deck)
    }

    private fun resetInitialDeckSize(): Game = copy(initialDeckSize = deck.size)

    private fun nextRound(): Game {
        if (round == Round.DescribeInOneWord)
            return copy(state = GameState.Finished)
        return copy(round = Round.values()[round.ordinal + 1])
            .resetDeck()
            .nextTeamInternal()
    }
}

private var existingIds: Set<String> = emptySet()

fun newId(retry: Int = 0): String {
    if (retry > 50)
        return UUID.randomUUID().toString()
    val tryId = UUID.randomUUID().toString().split("-")[0]
    if (existingIds.contains(tryId))
        return newId(retry + 1)
    return tryId
}

fun newGame(settings: GameSettings): Game {
    if (settings.playersCount % 2 != 0)
        throw IllegalArgumentException("There should be even number of players.")
    val id = newId()
    return Game.build(id, settings)
}

fun newPlayer(name: String): Player {
    return Player(newId(), name, emptySet(), PlayerState.SelectingWords, 0)
}