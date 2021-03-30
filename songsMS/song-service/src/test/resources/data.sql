insert into songs(id,title,artist,label,released) values (1,'test','test','test',1985);
insert into songs(id,title,artist,label,released) values (2,'test','test','test',1985);
insert into songs(id,title,artist,label,released) values (3,'test','test','test',1985);

insert into songlist(id,is_private,name,ownerid) values (1,false ,'testplalist','testuser');
insert into songlist(id,is_private,name,ownerid) values (6,true,'privateTest','otherUser');
insert into songlist(id,is_private,name,ownerid) values (4,false,'deleteTestPlayList','testuser');

insert into songlist(id,is_private,name,ownerid) values (2,false,'getAllTest1','owner');
insert into songlist(id,is_private,name,ownerid) values (3,false,'getAllTest2','owner');
insert into songlist(id,is_private,name,ownerid) values (5,false,'getAllTest3','owner');


insert into song_song_list(list_id, song_id) values (1,1);
insert into song_song_list(list_id, song_id) values (1,2);

insert into song_song_list(list_id, song_id) values (4,1);
insert into song_song_list(list_id, song_id) values (4,2);

insert into songs(id,title,artist,label,released) values (5,'We Built This City','Starship','Grunt/RCA',1985);
insert into songs(id,title,artist,label,released) values (4,'Sussudio','Phil Collins','Virgin',1985);