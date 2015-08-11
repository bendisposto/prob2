:- dynamic parserVersionNum/1, parserVersionStr/1, parseResult/5.
:- dynamic module/4.
'parserVersionStr'('0.6.1.1').
'parseResult'('ok','',0,0,0).
:- dynamic channel/2, bindval/3, agent/3.
:- dynamic agent_curry/3, symbol/4.
:- dynamic dataTypeDef/2, subTypeDef/2, nameType/2.
:- dynamic cspTransparent/1.
:- dynamic cspPrint/1.
:- dynamic pragma/1.
:- dynamic comment/2.
:- dynamic assertBool/1, assertRef/5, assertTauPrio/6.
:- dynamic assertModelCheckExt/4, assertModelCheck/3.
:- dynamic assertLtl/4, assertCtl/4.
'parserVersionNum'([0,10,1,2]).
'parserVersionStr'('CSPM-Frontent-0.10.1.2').
'channel'('a','type'('dotTupleType'(['setExp'('rangeClosed'('int'(1),'int'(10)))]))).
'bindval'('NonDeterm2','prefix'('src_span'(3,14,3,17,50,3),[],'dotTuple'(['a','int'(1)]),'prefix'('src_span'(3,21,3,24,57,3),[],'dotTuple'(['a','int'(2)]),'\x5c\'('[]'('val_of'('P','src_span'(3,30,3,31,66,1)),'val_of'('Q','src_span'(3,35,3,36,71,1)),'src_span_operator'('no_loc_info_available','src_span'(3,32,3,34,68,2))),'closure'(['a']),'src_span_operator'('no_loc_info_available','src_span'(3,38,3,39,74,1))),'src_span'(3,25,3,27,60,24)),'src_span'(3,18,3,20,53,31)),'src_span'(3,1,3,45,37,44)).
'assertModelCheckExt'('True','val_of'('NonDeterm2','src_span'(4,12,4,22,93,10)),'Deterministic','FD').
'bindval'('Determ1','prefix'('src_span'(5,11,5,14,136,3),[],'dotTuple'(['a','int'(1)]),'prefix'('src_span'(5,18,5,21,143,3),[],'dotTuple'(['a','int'(2)]),'[]'('val_of'('P','src_span'(5,26,5,27,151,1)),'val_of'('Q','src_span'(5,31,5,32,156,1)),'src_span_operator'('no_loc_info_available','src_span'(5,28,5,30,153,2))),'src_span'(5,22,5,24,146,15)),'src_span'(5,15,5,17,139,22)),'src_span'(5,1,5,33,126,32)).
'assertModelCheck'('True','val_of'('NonDeterm2','src_span'(6,12,6,22,170,10)),'LivelockFree').
'bindval'('NonDeterm3','[]'('prefix'('src_span'(7,15,7,18,212,3),[],'dotTuple'(['a','int'(1)]),'prefix'('src_span'(7,22,7,25,219,3),[],'dotTuple'(['a','int'(2)]),'val_of'('NonDeterm3','src_span'(7,29,7,39,226,10)),'src_span'(7,26,7,28,222,17)),'src_span'(7,19,7,21,215,24)),'prefix'('src_span'(7,45,7,48,242,3),[],'dotTuple'(['a','int'(1)]),'val_of'('P3','src_span'(7,52,7,54,249,2)),'src_span'(7,49,7,51,245,9)),'src_span_operator'('no_loc_info_available','src_span'(7,41,7,43,238,2))),'src_span'(7,1,7,55,198,54)).
'bindval'('P3','prefix'('src_span'(8,6,8,9,258,3),[],'dotTuple'(['a','int'(2)]),'val_of'('P','src_span'(8,13,8,14,265,1)),'src_span'(8,10,8,12,261,8)),'src_span'(8,1,8,14,253,13)).
'assertModelCheckExt'('True','val_of'('NonDeterm3','src_span'(9,12,9,22,278,10)),'Deterministic','F').
'bindval'('NDet','[]'('prefix'('src_span'(11,9,11,12,321,3),[],'dotTuple'(['a','int'(1)]),'stop'('src_span'(11,16,11,20,328,4)),'src_span'(11,13,11,15,324,11)),'prefix'('src_span'(11,26,11,29,338,3),[],'dotTuple'(['a','int'(1)]),'val_of'('NDet','src_span'(11,33,11,37,345,4)),'src_span'(11,30,11,32,341,11)),'src_span_operator'('no_loc_info_available','src_span'(11,22,11,24,334,2))),'src_span'(11,1,11,38,313,37)).
'bindval'('NDet1','prefix'('src_span'(12,9,12,12,359,3),[],'dotTuple'(['a','int'(1)]),'prefix'('src_span'(12,16,12,19,366,3),[],'dotTuple'(['a','int'(2)]),'prefix'('src_span'(12,23,12,26,373,3),[],'dotTuple'(['a','int'(3)]),'val_of'('NDet','src_span'(12,30,12,34,380,4)),'src_span'(12,27,12,29,376,11)),'src_span'(12,20,12,22,369,18)),'src_span'(12,13,12,15,362,25)),'src_span'(12,1,12,34,351,33)).
'assertModelCheckExt'('True','val_of'('NDet','src_span'(13,12,13,16,396,4)),'Deterministic','FD').
'assertModelCheckExt'('True','val_of'('NDet1','src_span'(14,12,14,17,434,5)),'Deterministic','F').
'bindval'('P','prefix'('src_span'(16,5,16,8,466,3),[],'dotTuple'(['a','int'(3)]),'[]'('prefix'('src_span'(16,13,16,16,474,3),[],'dotTuple'(['a','int'(4)]),'val_of'('P','src_span'(16,20,16,21,481,1)),'src_span'(16,17,16,19,477,8)),'stop'('src_span'(16,25,16,29,486,4)),'src_span_operator'('no_loc_info_available','src_span'(16,22,16,24,483,2))),'src_span'(16,9,16,11,469,25)),'src_span'(16,1,16,30,462,29)).
'bindval'('Q','prefix'('src_span'(17,5,17,8,496,3),[],'dotTuple'(['a','int'(6)]),'prefix'('src_span'(17,12,17,15,503,3),[],'dotTuple'(['a','int'(7)]),'stop'('src_span'(17,19,17,23,510,4)),'src_span'(17,16,17,18,506,11)),'src_span'(17,9,17,11,499,18)),'src_span'(17,1,17,23,492,22)).
'bindval'('PDIV','prefix'('src_span'(19,8,19,11,523,3),[],'dotTuple'(['a','int'(1)]),'val_of'('PDIV','src_span'(19,15,19,19,530,4)),'src_span'(19,12,19,14,526,11)),'src_span'(19,1,19,19,516,18)).
'assertModelCheckExt'('False','val_of'('NonDeterm2','src_span'(22,8,22,18,563,10)),'Deterministic','F').
'assertModelCheckExt'('True','val_of'('NonDeterm2','src_span'(23,12,23,22,606,10)),'Deterministic','FD').
'comment'('lineComment'('-- Deterministic1'),'src_position'(1,1,0,17)).
'comment'('lineComment'('--MAIN = NonDeterm2'),'src_position'(20,1,535,19)).
'symbol'('a','a','src_span'(2,9,2,10,26,1),'Channel').
'symbol'('NonDeterm2','NonDeterm2','src_span'(3,1,3,11,37,10),'Ident (Groundrep.)').
'symbol'('Determ1','Determ1','src_span'(5,1,5,8,126,7),'Ident (Groundrep.)').
'symbol'('NonDeterm3','NonDeterm3','src_span'(7,1,7,11,198,10),'Ident (Groundrep.)').
'symbol'('P3','P3','src_span'(8,1,8,3,253,2),'Ident (Groundrep.)').
'symbol'('NDet','NDet','src_span'(11,1,11,5,313,4),'Ident (Groundrep.)').
'symbol'('NDet1','NDet1','src_span'(12,1,12,6,351,5),'Ident (Groundrep.)').
'symbol'('P','P','src_span'(16,1,16,2,462,1),'Ident (Groundrep.)').
'symbol'('Q','Q','src_span'(17,1,17,2,492,1),'Ident (Groundrep.)').
'symbol'('PDIV','PDIV','src_span'(19,1,19,5,516,4),'Ident (Groundrep.)').