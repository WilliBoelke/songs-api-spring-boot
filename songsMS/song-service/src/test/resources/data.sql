insert into songs(id,title,artist,label,released) values (1,'test','test','test',1985);
insert into songs(id,title,artist,label,released) values (2,'test','test','test',1985);

insert into songlist(id,is_private,name,ownerid) values (1,false,'testplalist','mmuster');


insert into song_song_list(list_id, song_id) values (1,1);
insert into song_song_list(list_id, song_id) values (1,2)