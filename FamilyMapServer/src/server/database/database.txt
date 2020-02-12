CREATE TABLE IF NOT EXISTS AuthTokens (
  AuthToken TEXT NOT NULL,
  Username  TEXT NOT NULL,
  PRIMARY KEY (authToken),
  FOREIGN KEY (username) REFERENCES Users (username)
);

CREATE TABLE IF NOT EXISTS Users (
  username   TEXT NOT NULL,
  password   TEXT NOT NULL,
  email      TEXT NOT NULL,
  first_name TEXT NOT NULL,
  last_name  TEXT NOT NULL,
  gender     TEXT NOT NULL,
  personID   TEXT NOT NULL,
  PRIMARY KEY (username),
  FOREIGN KEY (personID) REFERENCES Persons (personID)
);

CREATE TABLE IF NOT EXISTS Events (
  eventID    TEXT NOT NULL,
  descendant TEXT NOT NULL,
  personID   TEXT NOT NULL,
  latitude   REAL NOT NULL,
  longitude  REAL NOT NULL,
  country    TEXT NOT NULL,
  city       TEXT NOT NULL,
  eventType  TEXT NOT NULL,
  year       TEXT NOT NULL,
  PRIMARY KEY (eventID),
  FOREIGN KEY (personID) REFERENCES Persons (personID),
  FOREIGN Key (descendant) REFERENCES Users (username)
);

CREATE TABLE IF NOT EXISTS Persons (
  personID   TEXT NOT NULL,
  descendant TEXT NOT NULL,
  first_name TEXT NOT NULL,
  last_name  TEXT NOT NULL,
  gender     TEXT NOT NULL,
  father     TEXT,
  mother     TEXT,
  spouse     TEXT,
  PRIMARY KEY (personID),
  FOREIGN KEY (descendant) REFERENCES Users (username)
);