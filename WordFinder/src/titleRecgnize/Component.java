package titleRecgnize;

/**
 * ����ͨ������ɵ����
 * @author YangYiyu
 *
 */
public class Component {
	private int id;
	private int top;
	private int bottom;
	private int left;
	private int right;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
	}

	/**
	 * ���캯��
	 * @param id
	 * @param x
	 * @param y
	 */
	public Component(int id, int x, int y) {
		super();
		this.id = id;
		this.top = y;
		this.bottom = y;
		this.left = x;
		this.right = x;
	}
	
	/**
	 * ��һ���µĵ���뵽�����
	 * @param x
	 * @param y
	 */
	public void addNewPoint(int x, int y) {
		this.top = Math.min(top, y);
		this.bottom = Math.max(bottom, y);
		this.left = Math.min(left, x);
		this.right = Math.max(right, x);
	}
	
	/**
	 * ����һ������ϲ�
	 * @param another
	 */
	public void mergeWith(Component another) {
		this.top = Math.min(top, another.top);
		this.bottom = Math.max(bottom, another.bottom);
		this.left = Math.min(left, another.left);
		this.right = Math.max(right, another.right);
	}
	
	/**
	 * �Ƿ���ȫ������һ�����
	 * @param another
	 * @return
	 */
	public boolean include(Component another) {
		if(another == null)return false;
		
		if(this.top <= another.top && this.bottom >= another.bottom
				&& this.left <= another.left && this.right >= another.right)
			return true;
		else 
			return false;
	}
	
	/**
	 * �Ƿ�����һ��������ֽ���
	 * @param another
	 * @return
	 */
	public boolean overlap(Component another) {
		if(another == null)return false;
		
		if(this.bottom-another.top>=0 && another.bottom-this.top>=0
				&& this.right-another.left>=0 && another.right-this.left>=0)
			return true;
		else 
			return false;
	}
	
	public boolean near(Component another) {
		if(another == null)return false;
		
		int centerX, centerY;
		centerX = Math.abs((this.right+this.left)/2 - (another.right+another.left)/2);
		centerY = Math.abs((this.bottom+this.top)/2 - (another.bottom+another.top)/2);
		if(centerX>8 && centerY>8)return false;
		
		if(centerY < centerX) {
			if(Math.max(this.left-another.right, another.left-this.right) < 5)
				return true;
			else
				return false;
		}
		else {
			if(Math.max(this.top-another.bottom, another.top-this.bottom) < 5)
				return true;
			else
				return false;
		}
	}
	
	public boolean verticalNear(Component another) {
		if(another == null)return false;
		
		//ˮƽ����ͶӰ�����Ƿ�Ϊ�գ���Ϊ�ձ�ʾ������������¹�ϵ
		if(!((another.left >= this.left && another.left < this.right)
				|| (another.right > this.left && another.right <= this.right)
				|| (this.left >= another.left && this.left < another.right)
				|| (this.right > another.left && this.right <= another.right)))
			return false;

		int centerX = Math.abs((this.right+this.left)/2 - (another.right+another.left)/2);
		
		if(centerX < 5) {
			if(Math.max(this.top-another.bottom, another.top-this.bottom) < 5)
				return true;
			else
				return false;
		}
		return false;
	}
	
	public boolean horizontalNear(Component another) {
		if(another == null)return false;
		
		//��ֱ����ͶӰ�����Ƿ�Ϊ�գ���Ϊ�ձ�ʾ������������ҹ�ϵ
		if(!((another.top >= this.top && another.top < this.bottom)
				|| (another.bottom > this.top && another.bottom <= this.bottom)
				|| (this.top >= another.top && this.top < another.bottom)
				|| (this.bottom > another.top && this.bottom <= another.bottom)))
			return false;
		
		int centerY = Math.abs((this.bottom+this.top)/2 - (another.bottom+another.top)/2);
		
		if(centerY < 5) {
			/*
			if(Math.max(this.left-another.right, another.left-this.right) < 5)
				return true;
			else
				return false;
			*/
			return true;
		}
		
		return false;	
	}
}
