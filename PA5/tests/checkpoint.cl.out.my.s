	.data
	.align	2
	.globl	class_nameTab
	.globl	Main_protObj
	.globl	Int_protObj
	.globl	String_protObj
	.globl	bool_const0
	.globl	bool_const1
	.globl	_int_tag
	.globl	_bool_tag
	.globl	_string_tag
_int_tag:
	.word	0
_bool_tag:
	.word	0
_string_tag:
	.word	0
	.globl	_MemMgr_INITIALIZER
_MemMgr_INITIALIZER:
	.word	_NoGC_Init
	.globl	_MemMgr_COLLECTOR
_MemMgr_COLLECTOR:
	.word	_NoGC_Collect
	.globl	_MemMgr_TEST
_MemMgr_TEST:
	.word	0
	.word	-1
str_const11:
	.word	0
	.word	5
	.word	String_dispTab
	.word	int_const4
	.byte	0	
	.align	2
	.word	-1
str_const10:
	.word	0
	.word	6
	.word	String_dispTab
	.word	int_const1
	.ascii	"Main"
	.byte	0	
	.align	2
	.word	-1
str_const9:
	.word	0
	.word	6
	.word	String_dispTab
	.word	int_const6
	.ascii	"String"
	.byte	0	
	.align	2
	.word	-1
str_const8:
	.word	0
	.word	6
	.word	String_dispTab
	.word	int_const1
	.ascii	"Bool"
	.byte	0	
	.align	2
	.word	-1
str_const7:
	.word	0
	.word	5
	.word	String_dispTab
	.word	int_const0
	.ascii	"Int"
	.byte	0	
	.align	2
	.word	-1
str_const6:
	.word	0
	.word	5
	.word	String_dispTab
	.word	int_const5
	.ascii	"IO"
	.byte	0	
	.align	2
	.word	-1
str_const5:
	.word	0
	.word	6
	.word	String_dispTab
	.word	int_const6
	.ascii	"Object"
	.byte	0	
	.align	2
	.word	-1
str_const4:
	.word	0
	.word	7
	.word	String_dispTab
	.word	int_const7
	.ascii	"_prim_slot"
	.byte	0	
	.align	2
	.word	-1
str_const3:
	.word	0
	.word	7
	.word	String_dispTab
	.word	int_const8
	.ascii	"SELF_TYPE"
	.byte	0	
	.align	2
	.word	-1
str_const2:
	.word	0
	.word	7
	.word	String_dispTab
	.word	int_const8
	.ascii	"_no_class"
	.byte	0	
	.align	2
	.word	-1
str_const1:
	.word	0
	.word	8
	.word	String_dispTab
	.word	int_const9
	.ascii	"<basic class>"
	.byte	0	
	.align	2
	.word	-1
str_const0:
	.word	0
	.word	9
	.word	String_dispTab
	.word	int_const10
	.ascii	"tests/checkpoint.cl"
	.byte	0	
	.align	2
	.word	-1
int_const10:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	19
	.word	-1
int_const9:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	13
	.word	-1
int_const8:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	9
	.word	-1
int_const7:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	10
	.word	-1
int_const6:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	6
	.word	-1
int_const5:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	2
	.word	-1
int_const4:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	0
	.word	-1
int_const3:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	1
	.word	-1
int_const2:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	8
	.word	-1
int_const1:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	4
	.word	-1
int_const0:
	.word	0
	.word	4
	.word	Int_dispTab
	.word	3
	.word	-1
bool_const0:
	.word	0
	.word	4
	.word	Bool_dispTab
	.word	0
	.word	-1
bool_const1:
	.word	0
	.word	4
	.word	Bool_dispTab
	.word	1
class_nameTab:
	.word	str_const5
	.word	str_const6
	.word	str_const7
	.word	str_const8
	.word	str_const9
	.word	str_const10
class_objTab:
	.word	Object_protObj
	.word	Object_init
	.word	IO_protObj
	.word	IO_init
	.word	Int_protObj
	.word	Int_init
	.word	Bool_protObj
	.word	Bool_init
	.word	String_protObj
	.word	String_init
	.word	Main_protObj
	.word	Main_init
	.globl	heap_start
heap_start:
	.word	0
	.text
	.globl	Main_init
	.globl	Int_init
	.globl	String_init
	.globl	Bool_init
	.globl	Main.main
