/* Database for handling game history with any number of players in each game. */

PRAGMA foreign_keys=OFF;
DROP TABLE IF EXISTS PlayedGames;
DROP TABLE IF EXISTS GameHistories;
DROP TABLE IF EXISTS Players;
DROP TABLE IF EXISTS Actions;
PRAGMA foreign_keys=ON;

create table Players(
    playerName char(20),

    primary key (playerName)
);

create table GameHistories(
    gameId integer,
    playerName char(20),
    outcome char(20),

    foreign key (gameId) references PlayedGames(gameId),
    foreign key (playerName) references Players(playerName),
    primary key (gameId, playerName)
);

create table PlayedGames(
    gameId integer,
    gameDate date,

    primary key (gameId)
);

create table Actions(
    gameId integer,
    playerName char(20),
    turn integer,
    boardRow integer,
    boardCol integer,
    
    foreign key (gameId) references PlayedGames(gameId),
    foreign key (playerName) references Players(playerName),
    primary key (turn, gameId, playerName)
);
