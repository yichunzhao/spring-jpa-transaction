insert into CUSTOMER(id, first_name, last_name, email) values(10, 'Mike', 'Brown', 'mb@hotmail.com');
insert into CUSTOMER(id, first_name, last_name, email) values(11, 'Mia', 'Peterson', 'mp@hotmail.com');

insert into public.order(order_id, fk_customer) values(12, 10);
insert into public.order(order_id, fk_customer) values(13, 10);
insert into public.order(order_id, fk_customer) values(14, 11);

insert into public.order_item(order_item_id, content, fk_order ) values(15, 'iphone8'    ,12);
insert into public.order_item(order_item_id, content, fk_order ) values(17, 'diamond pan',13);
insert into public.order_item(order_item_id, content, fk_order ) values(18, 'hp printer' ,14);
insert into public.order_item(order_item_id, content, fk_order ) values(19, 'finish soap',14);

