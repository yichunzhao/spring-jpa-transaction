insert into CUSTOMER(id, first_name, last_name, email) values(10, 'Mike', 'Brown', 'mb@hotmail.com');
insert into CUSTOMER(id, first_name, last_name, email) values(11, 'Mia', 'Peterson', 'mp@hotmail.com');

insert into public.order(order_id, fk_customer, given_order_id) values(12, 10, 'f206202a-93d2-11eb-a8b3-0242ac130003');
insert into public.order(order_id, fk_customer, given_order_id) values(13, 10, 'f2062282-93d2-11eb-a8b3-0242ac130003');
insert into public.order(order_id, fk_customer, given_order_id) values(14, 11, 'f2062372-93d2-11eb-a8b3-0242ac130003');

insert into public.order_item(order_item_id, content, fk_order ) values(15, 'iphone8'    ,12);
insert into public.order_item(order_item_id, content, fk_order ) values(17, 'diamond pan',13);
insert into public.order_item(order_item_id, content, fk_order ) values(18, 'hp printer' ,14);
insert into public.order_item(order_item_id, content, fk_order ) values(19, 'finish soap',14);

