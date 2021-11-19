# Requirements and Clarifications

This is a conglomeration of all the clarified points and Requirements I could find by filtering on `a2`. Minor rewording, but the posts are all referenced.

Minor lexicon:

- Capabilities: refers to what the system can do (Login, Logout, etc.)
- Rules: refers to inviolable restrictions surrounding Capabilities, regardless of Permissions (no two users can be logged in at once, etc.)
- Permissions: refers to what is restricted to different user types (a buyer cannot list a game for sale, etc.)

## @666

1.  The upper limit of a user's credit is not "999,999" as per the specification, but 999,999.99.
2.  Where the concept of "days" is mentioned (e.g. "a game that was just put up for sale cannot be purchased until the following day."), the specification document is referring to each execution of your backend.
3.  Game IDs are not unique. They need only be unique to a seller. Two different sellers can each sell a game with the same title as the other.
4.  There is no RULE that a refund need match any purchase that was made in the past, nor any games in any user accounts. It is simply a transaction with an amount to credit a buyer and charge a seller. No game is returned from the former to the latter either.
5.  If trying to credit any user an amount that would increase his / her balance above the limit, raise the balance to the maximum allowable (#1) and print a warning. (Does not apply to refunds. See #42)
6.  An admin has all PERMISSIONS.
7.  The sell price (PPPPPP) field is the base price (sans-discount). The price of a game during a sale is calculated separately based on the discount percentage (DDDDD).

## @670

8.  The program must preserve its state somehow. The effects of changes made on one day (#2) should be visible on successive days.

## @672

9.  The daily transaction file is the input to your backend. It is comprised of a number of transactions as seen in the specification.

## @673

10. All new games are added to the system via sell transactions only.
11. Multiple users can possess the same game.
12. If a seller has insufficient funds to match the full value of a refund (even if he has a bit), the refund fails entirely and no portion of the funds is returned whatsoever.
13. It can safely be assumed that only one user will be logged in at a time.
14. All attempts to buy a game must be made on a valid listing.
15. Any number of design patterns (0 included) are permitted (though not necessarily beneficial).

## @675

16. AuctionSale has transaction code 07 and has the same format as login (XX UUUUUUUUUUUUUUU TT CCCCCCCCC).
17. A privileged transaction is a transaction that only admins can do. A semi-privileged transaction can be performed only by some accounts (or in limited fashion e.g. only adding credit to self).

## @677

18. If a transaction causes an error, the error should be reported and execution should continue.

## @678

19. User(@741) and Game IDs are case sensitive.
20. A game listed by a seller can be purchased any number of times.
21. Sellers should be able to add credits despite having nothing to spend them on.

## @679

22. Admins can issue themselves refunds.

## @680

23. For users with both buy and sell PERMISSIONs, any bought games reside separately from games listed for sale. That is, the games they buy are not automatically counted as "games existing on a seller's account" and available for sale. Only games added with a sell transaction are available for purchase (only from the user who added them).

## @682

24. ~~A game can only be removed from the store if the user is deleted.~~

## @684

25. There is no method to change a user's account type barring deleting the account and creating a new one.

## @685

26. Do not treat every execution of the program as the first day. (#2)

## @686

27. Estimation in the product backlog is the time it takes to complete a user story.
28. User story ID merely serves to identify each user story.
29. The produc backlog can be (colour) formatted for legibility.

## @688

30. All user / game name's can contain special characters (!, @, #, etc.)
31. No valid user / game name will be comprised of only whitespaces.
32. Leading whitespaces in user / game names are valid characters.

## @689

33. The system should function correctly even with no admins.
34. The system can have as many initial users as necessary.

## @692

35. All games go on sale contemporaneously when an auctionsale is initiated.
36. An admin cannot delete his / her own account.
37. Fatal errors are those which would crash the program.

## @696

38. There is no limit to the amount of money that can be refunded (bearing #5 in mind).

## @698

39. Auction sale can be toggled on or off as many times as desired over the course of each day (granted the user is privileged) and the effects are immediate (the store will be considered as in an auctionsale state from the next transaction until it is disabled).
40. Games being sold by different sellers with the same name are independent (#3). The price and discount need not match.

## @701

41. **DIRECT AND UNAMBIGUOUS CONFLICT WITH #5**

## @702

42. #5 does not apply to refunds, where the transaction fails entirely and an error is logged.

## @703

43. ???

## @707

44. Alongside #32, usernames can have whitespaces in the middle as well. The only stipulation is that every whitespace after the last non-whitespace character is treated as padding (bearing #31 in mind).
45. The format of the records in the daily.txt will always match that which is provided in the readme. Note #68.

## @710

46. Our backend should be able to receive command-line arguments. (for what?)
47. We can assume the file will always be named daily.txt and reside in our workspace if deemed necessary.
48. User story priorities are up to us to decide, but there's no loss of marks for a "right" or "wrong" order of precedence.
49. Our projects / workspaces can be called whatever we want.

## @711

50. ???
51. No authentication need be performed on login transactions in terms of passwords or the like. The user must still be confirmed to exist though.

## @712

52. A seller cannot list two games with the same title.
53. Logged in users should not be able to buy / sell games for other users. The logged in user is the one buying / selling the games, regardless of the buyer / seller indicated in the daily transaction record. Presumably, a check should be performed to ensure they are the same user.

## @713

54. Attempted deletion of a user should succeed regardless of whether the credit (and presumably user account permissions type) are correct. Presumably, a warning should be printed.

## @715

55. There is no way to change a username (presumably barring deletion à la #25).

## @718

56. The auction sale need neither inform users that a sale is ongoing, nor does it behave as an auction. It is merely the term used to indicated that there is an active sale and all games are discounted per #7.

## @719

57. There is no specific guideline on any implementation details whatsoever.

## @722

58. All your implementation files should be contained in a "src" directory in your project repository.

## @725

59. An account balance should never be negative.

## @726

60. An admin can delete any user (including other admins) with the exception of himself / herself.

## @727

61. No two users can have the same name, regardless of user account type.

## @731

62. The front end is a completely optional and bonus feature.

## @736

63. Usernames of deleted accounts are allowed to be recycled.

## @738

64. We are permitted to change the initial state of our database such that it comes pre-loaded with a god / admin user.

## @739

65. Game price must be positive.
66. The front-end should not be able to process transactions which violate the state of the database or back-end rules. (No negative balances / prices, don't allow login of non-existent users, etc.)
67. The discount is always calculated exclusively with the base price and percentage.
68. The maximum length of a game name is 25 characters, despite the readme transaction code only having 19 placeholder 'I's.

## @741

69. A buyer cannot buy two copies of the same game (a game whose ID exists in the buyer's inventory).

## @742

70. Admins can create and delete a user on the same day. (not endorsed)

## @751

71. An incorrect transaction code is a fatal error.

## @752

72. Constraint errors are errors that attempt to violate limits established on transactions (max credit, no two copies of same game, etc.)

## @775

73. The maximum number of characters to represent the discount is 5 (implying 100% discount is impossible).

## @755

74. Probability is a strict percentage in the daily transaction file ("15.00", "25.50", etc.). It is not a unit proportion ("00.15", etc.), or any other format.
75. We will be penalized if our system _requires_ an admin to function.

## @767

76. We will need to keep track of how much credit has been added to a user each day.

## @773

77. **DIRECT AND UNAMBIGUOUS CONFLICT WITH #32**

## @777

78. Fatal errors should be logged and the program should proceed with the daily.txt file.

## @788

79. Any library can be used, provided it is cited.
80. Provide complete, unambiguous instructions for how to install any libraries / dependencies in features.md.

## @794

81. The number of characters for each field in the daily transaction file should prioritise the instructions given in the detail section of the specification.

## @795

82. The addcredit transaction is the only transaction which impacts the max daily added credit constant of 1000.00 credits. Game purchase, account creation et al do not affect this value.

## @796

83. Users with both buy and sell PERMISSIONs cannot buy games from themselves.

## @797

84. Constraint error logs should identify the cause of the error.

## @805

85. A user with buy and sell functionality's listings are completely isolated from his / her purchased game inventory. Bought games do not immediately populate a slot in a shared inventory connected to the users sale functionality.

## @807

86. There should be instructions in the features.md file explaining how to manipulate the database without the create user transaction.

## @810

87. The account type for the create / delete user transactions is the account type of the newly created / deleted user, not the issuing user.

## @812

88. The name of the parent folder to our solution is arbitrary (src, main, etc.)

## @817

89. The credit transaction field takes the format "000000.00".

## @833

90. Merely printing errors is sufficient. Errors need not be logged to a file.

## @844

91. A deleted user's inventory is deleted alongside him / her.
92. A deleted seller's listings are deleted alongside him / her, but any purchased copies remain.

## @884

93. What about fractions caused by discount percentages? Can we truncate / floor extra decimal places?

## @890

94. As gifted games are treated as bought (@923), sell-only users cannot receive gifts.

## @901

95. Truncate / round down decimal places that exceed the second place.
96. If a username for the user conducting a transaction does not match the logged in user, print an error but proceed if not admin.

## @909

97. A game gifted to a user can be re-gifted to yet another user.

## @910

98. Front-end changes should not affect the database in real time, but merely be recorded to the daily.txt for later processing.

## @942

99. Admins are not exempt from having to wait a day to remove a game.

## @859
100. Our system must accept a remove game transaction so a game can be removed from a user's inventory.
101. Our system must accept a gift game transaction so a game can be gifted from one user to another.
102. The user code for both the aforementioned transactions is `XX IIIIIIIIIIIIIIIIIIIIIIIII UUUUUUUUUUUUUUU SSSSSSSSSSSSSSS`, where the username is optional for non-admin users of the former transaction.
